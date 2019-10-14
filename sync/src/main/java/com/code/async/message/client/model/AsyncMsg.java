package com.code.async.message.client.model;

import java.io.Serializable;

/**
 * 异步Notify消息
 *
 * @author shunhua
 * @date 2019-10-14 12:53
 */
public interface AsyncMsg extends Serializable {
    /**
     * Topic
     *
     * @return
     */
    String getTopic();

    /**
     * Tag
     *
     * @return
     */
    String getTag();

    /**
     * 消息内容
     *
     * @return
     */
    <T> T getContent();

    /**
     * MsgID
     *
     * @return
     */
    String getMsgID();

    /**
     * Key
     *
     * @return
     */
    String getKey();

    /**
     * 重试次数
     *
     * @return
     */
    int getReconsumeTimes();

    /**
     * 开始投递的时间
     *
     * @return
     */
    long getStartDeliverTime();

    /**
     * Message实体
     *
     * @return
     */
    <T> T getMessage();

    /**
     * 最初的MessageID。在消息重试时msgID会变
     *
     * @return
     */
    String getOriginMsgID();
}
