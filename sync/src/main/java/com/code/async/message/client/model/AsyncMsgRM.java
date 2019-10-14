package com.code.async.message.client.model;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.code.async.message.client.util.HessianUtil;
import com.code.async.message.client.util.MsgUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class AsyncMsgRM implements AsyncMsg {

    private static final long serialVersionUID = 1L;

    private Logger log = LoggerFactory.getLogger(AsyncMsgRM.class);

    private MessageExt messageExt;

    public AsyncMsgRM(MessageExt messageExt) {
        this.messageExt = messageExt;
    }

    /**
     * 取消息体，已经反序列化
     *
     * @return
     */
    @Override
    public <T> T getContent() {
        try {
            return (T) HessianUtil.deserialize(messageExt.getBody());
        } catch (IOException e) {
            log.error("AsyncMsgRM deserialize error!", e);
            return null;

        }
    }

    public MessageExt getMessageExt() {
        return messageExt;
    }

    @Override
    public String getTopic() {
        return messageExt.getTopic();
    }

    @Override
    public String getTag() {
        return messageExt.getTags();
    }

    @Override
    public String getKey() {
        return messageExt.getKeys();
    }

    /**
     * 取消息id
     *
     * @return
     */
    @Override
    public String getMsgID() {
        return messageExt.getMsgId();
    }

    /**
     * 取重试次数
     *
     * @return
     */
    @Override
    public int getReconsumeTimes() {
        return messageExt.getReconsumeTimes();
    }

    /**
     * 取开始投递的时间
     *
     * @return
     */
    @Override
    public long getStartDeliverTime() {
        return messageExt.getBornTimestamp();
    }

    @Override
    public String getOriginMsgID() {
        return MsgUtils.getOriginMsgId(messageExt);
    }

    @Override
    public MessageExt getMessage() {
        return messageExt;
    }

    @Override
    public String toString() {
        return messageExt.toString();
    }
}
