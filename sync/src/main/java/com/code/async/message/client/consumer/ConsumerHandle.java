package com.code.async.message.client.consumer;

import com.code.async.message.client.model.AsyncMsg;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public abstract class ConsumerHandle implements IConsumerHandle{

    // 支持 || 关系
    private String subExpression;

    /**
     * 处理消息Handle，业务逻辑在此处理
     *
     * @param message
     * @return true:处理成功;false:处理失败，重新投递
     */
    @Override
    public abstract boolean consume(AsyncMsg message);

    @Override
    public String getSubExpression() {
        return subExpression;
    }

    public void setSubExpression(String subExpression) {
        this.subExpression = subExpression;
    }
}
