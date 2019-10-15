package com.code.rocketmq.spring.boot;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.code.async.message.client.sender.SendManager;
import com.code.async.message.client.sender.SendManagerImplForRm;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
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

import java.util.List;

/**
 * @author shunhua
 * @date 2019-10-14
 */
@Configuration
@ConditionalOnClass(DefaultMQProducer.class)
@ConditionalOnProperty(prefix = "spring.rocketmq.producer", name = "topic")
@EnableConfigurationProperties(RocketMqProducerProperties.class)
public class RocketMqProducerAutoConfiguration {

    /**
     * 注入生产者相关的配置属性
     */
    @Autowired
    private RocketMqProducerProperties rocketMqProductProperties;

    @Configuration
    @ConditionalOnMissingBean(SendManager.class)
    class PrimaryRocketMqProductAutoConfiguration {

        /**
         * 创建 SendManagerImplForRm 实例
         *
         * @return
         */
        @Bean(initMethod = "start")
        @Primary
        public SendManagerImplForRm sendManagerImplForRm() {
            // 走到这里 SendManagerImplForRm 中的sendProducer（DefaultMQProducer）已经被赋值了，但时它的相关属性都是初始的默认值
            SendManagerImplForRm sendManager = new SendManagerImplForRm();
            // 分别获取配置文件中属性为 sendProducer(DefaultMQProducer)设置相关属性
            sendManager.setTopic(rocketMqProductProperties.getTopic());
            sendManager.setNamesrvAddr(rocketMqProductProperties.getNameServerAddress());
            sendManager.setProducerGroup(rocketMqProductProperties.getProducerGroup());
            if (rocketMqProductProperties.getRetryTimesWhenSendFailed() != null) {
                sendManager.setRetryTimesWhenSendFailed(rocketMqProductProperties.getRetryTimesWhenSendFailed());
            }
            if (rocketMqProductProperties.getMaxMessageSize() != null) {
                sendManager.setMaxMessageSize(rocketMqProductProperties.getMaxMessageSize());
            }
            return sendManager;
        }
    }

    /**
     * 创建 除了主生产者的实例（如果通过multi配置了的话）
     */
    @Configuration
    class MultiRocketMqProductAutoConfiguration implements ApplicationContextAware, BeanPostProcessor, InitializingBean {
        /**
         * 声明Bean的注册定义
         */
        private BeanDefinitionRegistry beanDefinitionRegistry;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
        }

        /**
         * 注册Bean
         * @throws Exception
         */
        public void registerMultRocketMq() throws Exception {
            // 获取处除了主生产者外的生产者
            List<RocketMqProducerProperties> multi = rocketMqProductProperties.getMulti();
            if (CollectionUtils.isEmpty(multi)) {
                return;
            }
            for (RocketMqProducerProperties rocketMqProductPropertie : multi) {
                String name = rocketMqProductPropertie.getName();
                if (StringUtils.isEmpty(name)) {
                    throw new IllegalArgumentException("配置多个rocketMq时必须指定name属性");
                }
                copyProperties(rocketMqProductProperties, rocketMqProductPropertie);
                beanDefinitionRegistry.registerBeanDefinition(name, createBeanDefinition(rocketMqProductPropertie, SendManagerImplForRm.class));
            }
        }

        /**
         * 属性拷贝
         *
         * @param rocketMqProductProperties
         * @param rocketMqProductPropertie
         */
        private void copyProperties(RocketMqProducerProperties rocketMqProductProperties, RocketMqProducerProperties rocketMqProductPropertie) {
            if (rocketMqProductPropertie.getTopic() == null) {
                rocketMqProductPropertie.setTopic(rocketMqProductProperties.getTopic());
            }
            if (rocketMqProductPropertie.getRetryTimesWhenSendFailed() == null) {
                rocketMqProductPropertie.setRetryTimesWhenSendFailed(rocketMqProductProperties.getRetryTimesWhenSendFailed());
            }
            if (rocketMqProductPropertie.getProducerGroup() == null) {
                rocketMqProductPropertie.setProducerGroup(rocketMqProductProperties.getProducerGroup());
            }
            if (rocketMqProductPropertie.getNameServerAddress() == null) {
                rocketMqProductPropertie.setNameServerAddress(rocketMqProductProperties.getNameServerAddress());
            }
            if (rocketMqProductPropertie.getMaxMessageSize() == null && rocketMqProductProperties.getMaxMessageSize() != null) {
                rocketMqProductProperties.setMaxMessageSize(rocketMqProductProperties.getMaxMessageSize());
            }
        }

        /**
         * 为除了主生产者外的生产者创建Bean定义，本质流程还是和SendManagerImplForRm 创建实例一样
         *
         * @param newProp
         * @param sendManagerImplForRmClass
         * @return
         */
        private BeanDefinition createBeanDefinition(RocketMqProducerProperties newProp, Class<SendManagerImplForRm> sendManagerImplForRmClass) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(sendManagerImplForRmClass);
            beanDefinitionBuilder.addPropertyValue("topic", newProp.getTopic());
            beanDefinitionBuilder.addPropertyValue("producerGroup", newProp.getProducerGroup());
            beanDefinitionBuilder.addPropertyValue("namesrvAddr", newProp.getNameServerAddress());
            if (newProp.getRetryTimesWhenSendFailed() != null) {
                beanDefinitionBuilder.addPropertyValue("retryTimesWhenSendFailed", newProp.getRetryTimesWhenSendFailed());
            }
            if (newProp.getMaxMessageSize() != null) {
                beanDefinitionBuilder.addPropertyValue("maxMessageSize", newProp.getMaxMessageSize());
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
            registerMultRocketMq();
        }
    }


}
