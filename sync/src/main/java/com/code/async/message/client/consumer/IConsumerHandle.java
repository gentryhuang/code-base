package com.code.async.message.client.consumer;

import com.code.async.message.client.model.AsyncMsg;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public interface IConsumerHandle {
    /**
     * tag
     *
     * @return
     */
    String getSubExpression();

    /**
     * 消费
     *
     * @param asyncMsg
     * @return
     */
    boolean consume(AsyncMsg asyncMsg);
}
