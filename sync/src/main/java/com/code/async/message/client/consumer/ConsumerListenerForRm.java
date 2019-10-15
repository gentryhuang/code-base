package com.code.async.message.client.consumer;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.*;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.code.async.message.client.model.AsyncMsgRM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import static com.code.async.message.client.util.MD5Util.stringIsEmpty;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class ConsumerListenerForRm extends BaseConsumerListener {
    private Logger logger = LoggerFactory.getLogger(ConsumerListenerForRm.class);
    /**
     * 消费者
     */
    private DefaultMQPushConsumer consumer;
    /**
     * 消费者所属的组
     */
    private String consumerGroup;
    /**
     * RocketMQ 的 NameSRV
     */
    private String namesrvAddr;
    /**
     * 消费模式。默认是集群模式
     */
    private MessageModel messageModel = MessageModel.CLUSTERING;
    /**
     * 一个新的订阅组默认第一次启动从队列的最后位置开始消费
     */
    private ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;
    /**
     * 暂停消费，日常环境可用（可在控制台配置消费者的启停）
     */
    public static volatile boolean suspend = false;
    /**
     * 回溯消费时间
     */
    private String consumeTimestamp;
    /**
     * 是否启动顺序消费组
     */
    private RegisterConsumeType registerConsumeType = RegisterConsumeType.CONCURRENTLY;
    /**
     * 消费消息线程，最小数目
     */
    private int consumeThreadMin = 20;

    /**
     * ConsumerListenerForRm实例化之后会调用该方法
     */
    @Override
    public void start() throws IOException, MQClientException {

        //广播模式采用动态消费组的方式
        if (messageModel.equals(MessageModel.BROADCASTING)) {
            consumerGroup = consumerGroup + InetAddress.getLocalHost().getHostAddress().replace(".", "_");
        }
        // 创建Push模式的消息者
        consumer = new DefaultMQPushConsumer(consumerGroup);
        // 设置namesrv的值
        consumer.setNamesrvAddr(namesrvAddr);
        // 设置消费消息线程，最小数目
        consumer.setConsumeThreadMin(consumeThreadMin);
        /** 订阅指定topic下tags */
        consumer.subscribe(topic, consumerHandle.getSubExpression());
        /**
         * Consumer第一次启动默认从队列尾部开始消费
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(consumeFromWhere);
        if (consumeFromWhere.equals(ConsumeFromWhere.CONSUME_FROM_TIMESTAMP) &&
                !stringIsEmpty(consumeTimestamp)) {
            consumer.setConsumeTimestamp(consumeTimestamp);
        }
        // 注册监听器
        switch (registerConsumeType) {
            case ORDERLY:
                consumer.registerMessageListener(new BaseListenerOrderly());
                break;
            case CONCURRENTLY:
            default:
                consumer.registerMessageListener(new BaseListenerConcurrently());
                break;
        }
        //  启动消费者
        consumer.start();
        logger.info("ConsumerListenerForRm started!topic:" + topic + ",expression:" + consumerHandle.getSubExpression() + "  consumerGroup:" + consumerGroup + "   namesrvAddr:" + namesrvAddr);
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }

    /**
     * 设置消息模型：集群模式/广播模式
     */
    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    /**
     * 一个新的订阅组第一次启动时，从队列的什么位置开始消费（默认从队列尾部开始消费）
     *
     * @param consumeFromWhere
     */
    public void setConsumeFromWhere(ConsumeFromWhere consumeFromWhere) {
        this.consumeFromWhere = consumeFromWhere;

    }

    /**
     * 当设置Consumer第一次启动为回溯消费时，回溯到哪个时间点.
     *
     * @param consumeTimestamp 数据格式如下，时间精度秒:20131223171201，表示2013年12月23日17点12分01秒
     */
    public void setConsumeTimestamp(String consumeTimestamp) {
        this.consumeTimestamp = consumeTimestamp;
    }

    /**
     * 注册的消息监听器类型（默认为CONCURRENTLY）
     *
     * @param registerConsumeType
     */
    public void setRegisterConsumeType(RegisterConsumeType registerConsumeType) {
        this.registerConsumeType = registerConsumeType;
    }

    public enum RegisterConsumeType {
        ORDERLY, CONCURRENTLY,
    }

    /**
     * 消费消息线程数目
     */
    public void setConsumeThreadMin(int consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    @Override
    public void close() {
        this.consumer.shutdown();
        logger.info("ConsumerListenerForRm closed !");
    }

    /**
     * 消息监听器 - 同一队列的消息并行消费
     */
    private class BaseListenerConcurrently implements MessageListenerConcurrently {
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                        ConsumeConcurrentlyContext context) {
            MessageExt msg = msgs.get(0);
            AsyncMsgRM asyncMsgRM = new AsyncMsgRM(msg);
            /** 消息监听器监听到消息后，会调用MultiConsumerHandle的consume方法，而MultiConsumerHandle中已经封装了 tag映射的handler Map集*/
            boolean ret = consumerHandle.consume(asyncMsgRM);
            if (!ret) {
                logger.error("consume MQ failed,msgID:" + msg.getMsgId());
            } else {
                logger.debug("consume MQ,msg:" + msg.toString() + ",handle result:success.");
            }
            return ret ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS :
                    ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    /**
     * 消息监听器 - 同一队列的消息同一时刻只能一个线程消费，可保证消息在同一队列严格有序消费
     */
    private class BaseListenerOrderly implements MessageListenerOrderly {
        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
            MessageExt msg = msgs.get(0);
            AsyncMsgRM asyncMsgRM = new AsyncMsgRM(msg);
            boolean ret = consumerHandle.consume(asyncMsgRM);
            if (!ret) {
                logger.error("consume MQ failed,msgID:" + msg.getMsgId());
            }
            return ret ? ConsumeOrderlyStatus.SUCCESS :
                    ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        }
    }
}
