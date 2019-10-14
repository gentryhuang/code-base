package com.code.async.message.client.consumer;

import com.alibaba.rocketmq.client.exception.MQClientException;

import java.io.IOException;
import java.util.Properties;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public abstract class BaseConsumerListener {
    /**
     * 消息处理器
     */
    protected IConsumerHandle consumerHandle;
    /**
     * 属性
     */
    protected Properties consumerProperties;
    /**
     * topic
     */
    protected String topic;

    /**
     * Listener启动
     */
    abstract void start() throws IOException, MQClientException;

    abstract void close();

    public void setConsumerHandle(IConsumerHandle consumerHandle) {
        this.consumerHandle = consumerHandle;
    }

    public void setConsumerProperties(Properties consumerProperties) {
        this.consumerProperties = consumerProperties;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return this.topic;
    }

    public IConsumerHandle getConsumerHandle() {
        return this.consumerHandle;
    }
}
