package com.code.async.message.client.sender;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.netty.NettySystemConfig;
import com.code.async.message.client.constants.NotifyDelayType;
import com.code.async.message.client.constants.TracerConfig;
import com.code.async.message.client.constants.Tracing;
import com.code.async.message.client.util.HessianUtil;
import com.code.async.message.client.util.ThrowableHandler;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class SendManagerImplForRm implements SendManager {
    private Logger log = LoggerFactory.getLogger(SendManagerImplForRm.class);

    /**
     * RocketMQ消息发送对象
     */
    protected DefaultMQProducer sendProducer;
    /**
     * Topic
     */
    private String topic;
    /**
     * 组名。同属一个组的消费者，并行消费。不同属一个组的消费者，广播方式消费
     */
    protected String producerGroup;
    /**
     * NameSrv 的地址
     */
    private String namesrvAddr;
    /**
     * 发送失败时，重试的次数
     */
    private int retryTimesWhenSendFailed = 2;
    /**
     * 最大消息数
     */
    private int maxMessageSize = 1024 * 1024 * 2;
    /**
     * 异步超时时间
     */
    private int asyncTimeOut = 3000;
    /**
     * 是否顺序消息, 如果是非顺序消息，当broker存储有问题时候，进行重试发送
     */
    private Boolean orderMessage = true;
    /**
     * 消息队列
     */
    private List<MessageQueue> messageQueueList;
    private AtomicInteger sendWhichQueue = new AtomicInteger(0);

    /**
     * 通过Spring创建SendManagerImplForRm 实例之后该方法会调用
     * @throws MQClientException
     */
    public void start() throws MQClientException {
        // 创建生产者对象
        sendProducer = new DefaultMQProducer(this.producerGroup);
        // 最大消息大小设定为2M，超出将会抛异常
        sendProducer.setMaxMessageSize(maxMessageSize);
        // 当发送失败时，重试的次数
        sendProducer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
        //如果是非顺序消息，当broker存储有问题时候，进行重试发送
        if (!orderMessage) {
            sendProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
        }
        // 设置namesrv的地址
        sendProducer.setNamesrvAddr(namesrvAddr);
        // 启动生产者
        sendProducer.start();
        log.info("sendProducer started!");
    }


    public String getTopic() {
        return this.topic;
    }

    /**
     * 发消息，此时发送key取md5(msgTopic+msgTag+内容)，默认采用Hessian序列化
     *
     * @param tag        消息标签
     * @param msg        消息内容
     * @param hessianSer 是否使用hessian序列化，默认不传值 false，使用hessian序列化，true 不使用hessian序列化。直接msg.toString().getBytes()
     * @return 序列id，出错时返回null
     */
    @Override
    public String sendMsg(String tag, Object msg, boolean... hessianSer) {
        return sendMsg(tag, msg, "", hessianSer);
    }

    /**
     * 发消息，此时发送key取md5(msgTopic+msgTag+内容)，默认采用Hessian序列化
     * 此方法能将消息用指定的queueSelector策略以及selectorArgs路由到某个队列上
     *
     * @param tag
     * @param msg
     * @param queueSelector
     * @param selectorArgs
     * @param key
     * @param hessianSer
     * @return
     */
    public String sendMsg(String tag, Object msg, MessageQueueSelector queueSelector, Object selectorArgs, String key, boolean... hessianSer) {
        String ret = null;
        try {
            Message message = packMsg(key, tag, msg, hessianSer);
            SendResult result = sendProducer.send(message, queueSelector, selectorArgs);
            ret = result.getMsgId();
            if (log.isDebugEnabled()) {
                log.debug("send message to broker success,msg:" + message.toString() + ",result:" + result.toString());
            }
        } catch (Exception e) {
            log.error("RocketMQ error!send message to broker failed,topic=" + topic + ",tag=" + tag + ",key=" + key, e);
        }
        return ret;
    }

    @Override
    public String sendMsg(String tag, Object msg, NotifyDelayType notifyDelayType, boolean... hessianSer) {
        return sendMsg(tag, msg, "", null, notifyDelayType, hessianSer);
    }

    /**
     * 发消息，默认采用Hessian序列化
     *
     * @param tag        消息标签
     * @param msg        消息内容
     * @param key        业务关键属性，用于查询&重发使用
     * @param hessianSer 是否使用hessian序列化，默认不传值 false，使用hessian序列化，true 不使用hessian序列化。直接msg.toString().getBytes()
     * @return 序列id，出错时返回null
     */
    @Override
    public String sendMsg(String tag, Object msg, String key, boolean... hessianSer) {
        return sendMsg(tag, msg, key, null, hessianSer);
    }

    /**
     * 发消息，默认采用Hessian序列化
     *
     * @param tag            消息标签
     * @param msg            消息内容
     * @param key            业务关键属性，用于查询&重发使用
     * @param userProperties 用户属性
     * @param hessianSer     是否使用hessian序列化，默认不传值 false，使用hessian序列化，true 不使用hessian序列化。直接msg.toString().getBytes()
     * @return 序列id，出错时返回null
     */
    @Override
    public String sendMsg(String tag, Object msg, String key, Map<String, String> userProperties, boolean... hessianSer) {
        return sendMsg(tag, msg, key, userProperties, null, hessianSer);
    }

    /**
     * 发消息，默认采用Hessian序列化
     *
     * @param tag            消息标签
     * @param msg            消息内容
     * @param key            业务关键属性，用于查询&重发使用
     * @param userProperties 用户属性
     * @param hessianSer     是否使用hessian序列化，默认不传值 false，使用hessian序列化，true 不使用hessian序列化。直接msg.toString().getBytes()
     * @return 序列id，出错时返回null
     * @see NotifyDelayType
     */
    @Override
    public String sendMsg(String tag, Object msg, String key, Map<String, String> userProperties, NotifyDelayType notifyDelayType, boolean... hessianSer) {
        return sendMsg(tag, msg, key, userProperties, notifyDelayType, SendWay.SYNC, null, hessianSer);
    }

    private String sendMsg(String tag, Object msg, String key, Map<String, String> userProperties, NotifyDelayType notifyDelayType, SendWay sendWay, final SendCallback sendCallback, boolean... hessianSer) {
        Span span = buildSpan("sendMsg", tag, sendWay);
        String ret = null;
        try {
            if (key == null || key.isEmpty()) {
                key = UUID.randomUUID().toString().replace("-", "");
            }
            Message message = packMsg(key, tag, msg, hessianSer);
            if (notifyDelayType != null) {
                message.setDelayTimeLevel(notifyDelayType.getLevel());
            }
            if (userProperties != null) {
                for (Map.Entry<String, String> entry : userProperties.entrySet()) {
                    message.putUserProperty(entry.getKey(), entry.getValue());
                }
            }
            switch (sendWay) {
                case SYNC:
                    SendResult result = sendProducer.send(message);
                    ret = result.getMsgId();
                    span.setTag(TracerConfig.MSG_ID, ret);
                    log.debug("send message to broker success,msg:" + message.toString() + ",result:" + result.toString());
                    break;
                case ASYNC:
                    AtomicInteger retryTimes = new AtomicInteger();
                    doAsyncSendMessageImpl(message, null, sendCallback, retryTimes);
                    break;
                case ONEWAY:
                    sendProducer.sendOneway(message);
                    break;
            }

        } catch (Exception e) {
            log.error("RocketMQ error!send message to broker failed,topic=" + topic + ",tag=" + tag + ",key=" + key, e);
            ThrowableHandler.handle(span, e);
        } finally {
            span.finish();
        }
        return ret;
    }

    private Span buildSpan(String operationName, String tag, SendWay sendWay) {
        Tracer tracer = Tracing.current().tracer();
        Span span = tracer.buildSpan(operationName).startManual();
        Tags.COMPONENT.set(span, TracerConfig.MQ_COMPONENT);
        span.setTag(TracerConfig.TOPIC, this.topic);
        span.setTag(TracerConfig.PRODUCER_GROUP, this.producerGroup);
        span.setTag(TracerConfig.TAG, tag);
        if (sendWay != null) {
            span.setTag(TracerConfig.SEND_WAY, sendWay.toString());
        }
        return span;
    }

    /**
     * 发消息，默认采用Hessian序列化
     *
     * @param tag            消息标签
     * @param msg            消息内容
     * @param userProperties 用户属性
     * @param hessianSer     是否使用hessian序列化，默认不传值 false，使用hessian序列化，true 不使用hessian序列化。直接msg.toString().getBytes()
     * @return 序列id，出错时返回null
     */
    @Override
    public String sendMsg(String tag, Object msg, Map<String, String> userProperties, boolean... hessianSer) {
        return sendMsg(tag, msg, "", userProperties, hessianSer);
    }

    /**
     * MQ不提供p2p mqtt 消息服务
     *
     * @param msg  消息内容
     * @param tags 消息标签
     * @return
     */
    @Override
    @Deprecated
    public void sendP2pMqttMsg(String msg, String... tags) {
    }

    /**
     * 发送消息，异步调用
     *
     * @param tag          消息标签
     * @param msg          消息内容
     * @param sendCallback 发送结果通过此接口回调
     */
    @Override
    public void sendMsgAsync(String tag, Object msg, SendCallback sendCallback) {
        sendMsg(tag, msg, null, null, null, SendWay.ASYNC, sendCallback);
    }

    @Override
    public void sendMsgOneWay(String tag, Object msg) {
        sendMsg(tag, msg, null, null, null, SendWay.ONEWAY, null);
    }

    /**
     * 异步发送消息，重试机制完善
     *
     * @param message
     * @param mq
     * @param sendCallback
     * @param retryTimes
     */
    private void doAsyncSendMessageImpl(final Message message, MessageQueue mq, final SendCallback sendCallback, final AtomicInteger retryTimes) throws MQClientException {

        String lastBrokerName = null == mq ? null : mq.getBrokerName();
        final MessageQueue selectOneMessageQueue = selectOneMessageQueue(lastBrokerName);
        try {

            sendProducer.send(message, selectOneMessageQueue, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    sendCallback.onSuccess(sendResult);
                }

                @Override
                public void onException(Throwable e) {
                    asyncResend(message, selectOneMessageQueue, sendCallback, retryTimes, e);
                }
            }, asyncTimeOut);
        } catch (Exception e) {
            asyncResend(message, selectOneMessageQueue, sendCallback, retryTimes, e);
        }
    }

    /**
     * 异步消息失败后重试
     *
     * @huoshao
     */
    private void asyncResend(final Message message, final MessageQueue messageQueue, final SendCallback sendCallback, final AtomicInteger retryTimes, Throwable e) {
        if (retryTimes.getAndIncrement() <= retryTimesWhenSendFailed) {
            log.warn("resend " + retryTimes.get() + " times, still failed, brokername:" + messageQueue.getBrokerName() + ",Topic: " + message.getTopic() + ", tag=" + message.getTags() + ",key=" + message.getKeys(), e.getMessage());
            try {
                doAsyncSendMessageImpl(message, messageQueue, sendCallback, retryTimes);
            } catch (MQClientException me) {
                log.error("MQClient exception, broker :" + messageQueue.getBrokerName() + " topic: " + message.getTopic());
                sendCallback.onException(e);
            }

        } else {
            log.error("RocketMQ send error, resend " + retryTimes.get() + " times, still failed, brokername: " + messageQueue.getBrokerName() + ",Topic: " + message.getTopic() + ", tag=" + message.getTags() + ",key=" + message.getKeys(), e.getMessage());
            sendCallback.onException(e);
        }
    }

    /**
     * 如果lastBrokerName不为null，则寻找与其不同的MessageQueue
     */
    public MessageQueue selectOneMessageQueue(final String lastBrokerName) throws MQClientException {
        if (messageQueueList == null) {
            messageQueueList = sendProducer.fetchPublishMessageQueues(topic);
        }
        if (lastBrokerName != null) {
            int index = this.sendWhichQueue.getAndIncrement();
            for (int i = 0; i < messageQueueList.size(); i++) {
                int pos = Math.abs(index++) % messageQueueList.size();
                MessageQueue mq = messageQueueList.get(pos);
                if (!mq.getBrokerName().equals(lastBrokerName)) {
                    return mq;
                }
            }

            return null;
        } else {
            int index = this.sendWhichQueue.getAndIncrement();
            int pos = Math.abs(index) % messageQueueList.size();
            return messageQueueList.get(pos);
        }
    }

    /**
     * 组装 Message
     *
     * @param key
     * @param tag
     * @param msg
     * @param hessianSer
     * @return
     * @throws IOException
     */

    protected Message packMsg(String key, String tag, Object msg, boolean... hessianSer) throws IOException {
        byte[] body;
        if (hessianSer.length > 0 && hessianSer[0]) {
            body = msg.toString().getBytes();
        } else {
            body = HessianUtil.serialize(msg);
        }
        return new Message(topic, tag, key, body);
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * 异步发送时，等待获取信号量的超时时间
     *
     * @param asyncTimeOut
     */
    public void setAsyncTimeOut(int asyncTimeOut) {
        this.asyncTimeOut = asyncTimeOut;
    }

    public void setAsyncSemaphoreSize(int val) {
        System.setProperty(NettySystemConfig.SystemPropertyClientAsyncSemaphoreValue, String.valueOf(val));
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public int getRetryTimesWhenSendFailed() {
        return retryTimesWhenSendFailed;
    }

    public void setRetryTimesWhenSendFailed(int retryTimesWhenSendFailed) {
        this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
    }

    public int getMaxMessageSize() {
        return maxMessageSize;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    protected void setSendProducer(DefaultMQProducer sendProducer) {
        this.sendProducer = sendProducer;
    }

    protected String getProducerGroup() {
        return producerGroup;
    }

    public Boolean getOrderMessage() {
        return orderMessage;
    }

    public void setOrderMessage(Boolean orderMessage) {
        this.orderMessage = orderMessage;
    }

    public void close() {
        this.sendProducer.shutdown();
    }

    private enum SendWay {
        ONEWAY, ASYNC, SYNC
    }

}
