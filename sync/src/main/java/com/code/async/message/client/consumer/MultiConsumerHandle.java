package com.code.async.message.client.consumer;

import com.code.async.message.client.consumer.annotation.MessageTag;
import com.code.async.message.client.model.AsyncMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 同Topic的多个Consumer可以一起设置
 * <p>如果不是通过配置文件而是Xml配置，方法如下,使用配置文件的情况见/config/Xxx.xml</p>
 * <blockquote><pre>
 *  <bean id="businessMultiConsumerHandle" class="com.code.async.message.client.consumer.MultiConsumerHandle">
 *        //
 *        <property name="callbackList">
 *            <list>
 *                //多个实现{@link ConsumerCallBack}的bean配置好,一个ConsumerListener就能分别处理同Topic同一个Consumer多个Tag的消息
 *                <bean class="com.code.notify.XXXXBusinessCallBack"/>
 *                <bean class="com.code.notify.YYYYBusinessCallBack"/>
 *            </list>
 *        </property>
 *        <property name="topic" value="${ons.topic}"/>
 *   </bean>
 * </pre></blockquote>
 *
 * @author shunhua
 * @date 2019-10-14
 */
public class MultiConsumerHandle extends ConsumerHandle {

    private Logger log = LoggerFactory.getLogger(MultiConsumerHandle.class);

    /**
     * key : tag 标签
     * value: tag对应的处理器
     */
    private Map<String, ConsumerCallBack> callbackCaches = new ConcurrentHashMap<>();
    /**
     * 多个Tag分隔符,RocketMQ默认为"||"
     */
    private String tagSplit = "||";

    /**
     * 消息监听器监听到消息时就会调用该方法，然后该放方会根据消息的tag选择对应的消息处理器处理该消息
     *
     * @param message
     * @return
     */
    @Override
    public boolean consume(AsyncMsg message) {
        boolean result = true;
        try {
            // 获取tag标签对应的处理器
            ConsumerCallBack consume = callbackCaches.get(message.getTag());
            //没有相应tag的消费处理类,不应该被接收处理到的,可能出现异常投递过来的.
            if (consume == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Tag[" + message.getTag() + "]该类型对应的的ConsumeCallback不存在,不处理,如要处理,请配置在callbackList中.");
                }
                return result;
            }
            /**
             * 处理器hangler的处理方法
             * 注意：process是ConsumerCallBack接口中的方法，而AbstractNotifyCallBack抽象类实现了该接口，在process的方法中利用模版方法模式，
             *      暴露一个给子类的handle方法用来真正处理消息。
             */
            result = consume.process(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = false;
        }
        return result;
    }

    public void setTagSplit(String tagSplit) {
        this.tagSplit = tagSplit;
    }

    /**
     * 配置多tag对应的消息处理类
     *
     * @param callbackList
     */
    public void setCallbackList(List<ConsumerCallBack> callbackList) {
        if (callbackList == null || callbackList.isEmpty()) {
            return;
        }
        StringBuffer _subExpression = new StringBuffer();
        for (ConsumerCallBack consume : callbackList) {
            if (consume == null) {
                continue;
            }
            Class<?> clazz = consume.getClass();
            if (isProxyBean(consume)) {
                clazz = AopUtils.getTargetClass(consume);
            }
            MessageTag messageTag = clazz.getAnnotation(MessageTag.class);
            if (messageTag == null) {
                log.error("配置ConsumeCallback的messageTag为空,请检查后配置:" + consume);
            }
            String[] tags = messageTag.tag();
            if (tags == null || tags.length == 0) {
                log.error("配置的messageTag没有添加具体类型的tag,请检查后配置(使用方法:@MessagetTag(tag={\"xxx\",\"yyy\"})):" + consume);
            }
            for (String tag : tags) {
                callbackCaches.put(tag, consume);
                _subExpression.append(tag).append(tagSplit);
            }
        }
        String subExpression = _subExpression.toString();
        if (subExpression.endsWith(tagSplit)) {
            subExpression = subExpression.substring(0, subExpression.length() - tagSplit.length());
        }
        log.debug("已订阅:[" + subExpression + "]这些类型的消息");
        /** 只接收配置了相应Tag的消息，调用父类的setSubExpression方法 */
        setSubExpression(subExpression);
    }

    private boolean isProxyBean(Object bean) {
        return AopUtils.isAopProxy(bean);
    }

}
