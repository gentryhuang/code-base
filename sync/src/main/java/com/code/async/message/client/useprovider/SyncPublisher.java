package com.code.async.message.client.useprovider;

import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.code.async.message.client.constants.SyncConstants;
import com.code.async.message.client.sender.SendManager;
import com.code.common.logger.CommLoggerFactory;
import com.code.common.logger.CommLoggerMarkers;
import com.code.common.logger.LoggerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 *
 * @author shunhua
 * @date 2019-10-14
 */
@Component
public class SyncPublisher {

    @Resource
    private SendManager sendManager;

    /**
     * 消息发送
     *
     * @param tag
     * @param message
     * @param logMsg
     * @return
     */
    private String sendMsg(String tag, Object message, String logMsg) {
        String msgId = StringUtils.EMPTY;
        try {
            msgId = sendManager.sendMsg(tag, message);
            LoggerUtil.info(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS, logMsg);
            if (StringUtils.isBlank(msgId)) {
                LoggerUtil.error(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS, "消息发送失败");
            }
            return msgId;
        } catch (Exception e) {
            LoggerUtil.error(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS, "消息发送失败");
        }
        return msgId;
    }

    /**
     * 消息发送
     *
     * @param tag
     * @param message
     * @param logMsg
     */
    private void sendMsgAsynce(String tag, Object message, String logMsg) {
        try {
            sendManager.sendMsgAsync(tag, message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    LoggerUtil.info(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS, logMsg);
                }

                @Override
                public void onException(Throwable e) {
                    LoggerUtil.info(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS, "消息发送失败");
                }
            });
        } catch (Exception e) {
            LoggerUtil.info(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS, "消息发送失败");
        }
    }

    //---------------------- 消息发送方法 ------------------/

    public void testSendMsgAsync(Object object) {
        sendMsgAsynce(SyncConstants.Tag.BUSINESS_TAG, object.toString(), "helloworld");
    }

    public String testSendMsg(Object object){
        return sendMsg(SyncConstants.Tag.BUSINESS_TAG,object.toString(),"helloworld");
    }

}
