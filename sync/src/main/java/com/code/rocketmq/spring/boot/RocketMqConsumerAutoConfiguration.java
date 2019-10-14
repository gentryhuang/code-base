package com.code.rocketmq.spring.boot;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.code.async.message.client.consumer.ConsumerCallBack;
import com.code.async.message.client.consumer.ConsumerListenerForRm;
import com.code.async.message.client.consumer.IConsumerHandle;
import com.code.async.message.client.consumer.MultiConsumerHandle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shunhua
 * @date 2019-10-14
 */
@Configuration
@ConditionalOnClass(DefaultMQPushConsumer.class)
@ConditionalOnProperty(prefix = "spring.rocketmq.consumer", name = "topic")
@EnableConfigurationProperties(RocketMqConsumerProperties.class)
@AutoConfigureAfter(IConsumerHandle.class)
public class RocketMqConsumerAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private RocketMqConsumerProperties rocketMqConsumerProperties;

    @Configuration
    @ConditionalOnMissingBean(ConsumerListenerForRm.class)
    class PrimaryRocketMqConsumerAutoConfiguration {

        /**
         * 实例化 ConsumerListenerForRm
         *
         * @return
         */
        @Bean(initMethod = "start")
        @Primary
        public ConsumerListenerForRm consumerListenerForRm() {
            // 获取配置文件中的处理器的bean 名称列表
            String[] consumerNames = rocketMqConsumerProperties.getConsumerHandle();
            if (consumerNames == null || consumerNames.length == 0) {
                throw new IllegalArgumentException("mq ConsumerCallBack can't be null");
            }
            List<ConsumerCallBack> consumerCallBacks = new ArrayList<>();
            for (String consumerName : consumerNames) {
                // 根据名称获取对应的处理器
                ConsumerCallBack consumerCallBack = (ConsumerCallBack) applicationContext.getBean(consumerName);
                if (consumerCallBack == null) {
                    continue;
                }
                consumerCallBacks.add(consumerCallBack);
            }
            // 同Topic的多个Consumer可以一起设置
            MultiConsumerHandle multiConsumerHandle = new MultiConsumerHandle();
            // 配置多tag对应的消息处理类
            multiConsumerHandle.setCallbackList(consumerCallBacks);
            ConsumerListenerForRm consumerListenerForRm = new ConsumerListenerForRm();
            consumerListenerForRm.setTopic(rocketMqConsumerProperties.getTopic());
            consumerListenerForRm.setNamesrvAddr(rocketMqConsumerProperties.getNameServerAddress());
            consumerListenerForRm.setConsumerGroup(rocketMqConsumerProperties.getConsumerGroup());
            consumerListenerForRm.setConsumerHandle(multiConsumerHandle);
            if (rocketMqConsumerProperties.getMessageModel() != null) {
                consumerListenerForRm.setMessageModel(rocketMqConsumerProperties.getMessageModel());
            }

            return consumerListenerForRm;
        }

    }

    /**
     * 配置其他的消费者
     */
    @Configuration
    class MultiRocketMqConsumerAutoConfiguration implements BeanPostProcessor, InitializingBean {
        /**
         * 注册其他的消费者
         *
         * @param beanDefinitionRegistry
         * @throws Exception
         */
        public void registerMultRocketMq(BeanDefinitionRegistry beanDefinitionRegistry) throws Exception {
            List<RocketMqConsumerProperties> multi = rocketMqConsumerProperties.getMulti();
            if (CollectionUtils.isEmpty(multi)) {
                return;
            }
            for (RocketMqConsumerProperties consumerProperties : multi) {
                String name = consumerProperties.getName();
                if (StringUtils.isEmpty(name)) {
                    throw new IllegalArgumentException("配置多个rocketMq时必须指定name属性");
                }
                copyProperties(rocketMqConsumerProperties, consumerProperties);
                beanDefinitionRegistry.registerBeanDefinition(name, createBeanDefinition(consumerProperties, ConsumerListenerForRm.class));

            }
        }

        private void copyProperties(RocketMqConsumerProperties rocketMqConsumerProperties, RocketMqConsumerProperties consumerProperties) {
            if (consumerProperties.getTopic() == null) {
                consumerProperties.setTopic(rocketMqConsumerProperties.getTopic());
            }
            if (consumerProperties.getConsumerGroup() == null) {
                consumerProperties.setConsumerGroup(rocketMqConsumerProperties.getConsumerGroup());
            }
            if (consumerProperties.getNameServerAddress() == null) {
                consumerProperties.setNameServerAddress(rocketMqConsumerProperties.getNameServerAddress());
            }
            if (consumerProperties.getConsumerHandle() == null) {
                consumerProperties.setConsumerHandle(rocketMqConsumerProperties.getConsumerHandle());
            }
            if (consumerProperties.getMessageModel() == null && rocketMqConsumerProperties.getMessageModel() != null) {
                consumerProperties.setMessageModel(rocketMqConsumerProperties.getMessageModel());
            }
        }

        private <T> BeanDefinition createBeanDefinition(RocketMqConsumerProperties newProp, Class<T> clazz) {
            String[] consumerNames = newProp.getConsumerHandle();
            if (consumerNames == null || consumerNames.length == 0) {
                throw new IllegalArgumentException("mq ConsumerCallBack can't be null");
            }
            List<ConsumerCallBack> consumerCallBacks = new ArrayList<ConsumerCallBack>();
            for (String consumerName : consumerNames) {
                ConsumerCallBack consumerCallBack = (ConsumerCallBack) applicationContext.getBean(consumerName);
                if (consumerCallBack == null) {
                    continue;
                }
                consumerCallBacks.add(consumerCallBack);
            }
            MultiConsumerHandle multiConsumerHandle = new MultiConsumerHandle();
            multiConsumerHandle.setCallbackList(consumerCallBacks);
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            beanDefinitionBuilder.addPropertyValue("consumerHandle", multiConsumerHandle);
            beanDefinitionBuilder.addPropertyValue("topic", newProp.getTopic());
            beanDefinitionBuilder.addPropertyValue("consumerGroup", newProp.getConsumerGroup());
            beanDefinitionBuilder.addPropertyValue("namesrvAddr", newProp.getNameServerAddress());
            if (newProp.getMessageModel() != null) {
                beanDefinitionBuilder.addPropertyValue("messageModel", newProp.getMessageModel());
            }

            beanDefinitionBuilder.setInitMethodName("start");
            return beanDefinitionBuilder.getRawBeanDefinition();
        }


        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
            registerMultRocketMq(beanDefinitionRegistry);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
