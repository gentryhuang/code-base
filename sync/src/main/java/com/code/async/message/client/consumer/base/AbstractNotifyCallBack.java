package com.code.async.message.client.consumer.base;

import com.alibaba.common.convert.Convert;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.code.async.message.client.constants.SyncConstants;
import com.code.async.message.client.consumer.ConsumerCallBack;
import com.code.async.message.client.model.AsyncMsg;
import com.code.async.message.client.util.MD5Util;
import com.code.cache.jedis.cache.ICacheService;
import com.code.common.exception.ExceptionPicker;
import com.code.common.logger.CommLoggerFactory;
import com.code.common.logger.LoggerUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.annotation.Resource;

/**
 * AbstractNotifyCallBack使用模版方法设计模式，handle方法让子类取实现。需要注意的是，消息监听回调方法调用的是ConsumerCallBack#process方法，
 * handle方法在AbstractNotifyCallBack的process中，即这就要求我们在使用的使用要继承AbstractNotifyCallBack，然后重新handle方法即可。
 *
 * @author shunhua
 * @date 2019-10-14
 */
public abstract class AbstractNotifyCallBack implements ConsumerCallBack {

    @Resource
    private ICacheService cacheService;

    public Logger loggerCallBack = CommLoggerFactory.MESSAGE;

    @Override
    public boolean process(AsyncMsg msg) {
        try {
            //过滤消息
            if (filterMsg(msg)) {
                return true;
            }
            handle(msg);
            //记录消费日志
            // ...
        } catch (Throwable e) {
            handleException(msg, e);
            return true;
        }
        return true;
    }


    /**
     * 子类处理具体业务逻辑
     *
     * @param msg
     * @return
     */
    protected abstract boolean handle(AsyncMsg msg) throws Exception;

    /**
     * 过滤消息
     * true 表示消息过滤成功
     * false 表示消息过滤失败
     *
     * @param msg
     * @return boolean
     */
    protected boolean filterMsg(AsyncMsg msg) {
        //消息为空
        if (msg == null || StringUtils.isBlank(msg.getTag()) || msg.getContent() == null) {
            return true;
        }
        //增加消息时效性 5分钟
        Object o = msg.getMessage();
        if (o instanceof MessageExt) {
            MessageExt messageExt = (MessageExt) o;
            long intervalTime = System.currentTimeMillis() - messageExt.getBornTimestamp();
            if (intervalTime > SyncConstants.CacheTime.MSG_EXPIRE_INTERVAL_TIME) {
                // 不做业务处理
                return true;
            }
        }

        //防止重复消费相同消息ID 5分钟
        if (cacheService.setnx(createMsgLockKey(msg), 5 * 60, "1") == 0) {
            // 不做业务处理
            return true;
        }
        //防止重复推送相同内容 3秒
        String contentMd5 = MD5Util.getMD5String(Convert.asString(msg.getContent()) + msg.getTag());
        if (cacheService.setnx(SyncConstants.CacheKey.REPEAT_CONSUMER_MQ_MSG_KEY + contentMd5, 3, "1") == 0) {
            // 不做业务处理
            return true;
        }
        return false;
    }

    protected String createMsgLockKey(AsyncMsg msg) {
        String keyMd5 = MD5Util.getMD5String(msg.getTag() + msg.getMsgID() + msg.getKey());
        return SyncConstants.CacheKey.REPEAT_CONSUMER_MQ_MSG_KEY + ":" + keyMd5;
    }

    /**
     * 处理消息异常信息
     *
     * @param msg
     * @return boolean
     */
    protected void handleException(AsyncMsg msg, Throwable e) {
        String exMsg = ExceptionPicker.pickup(e);
        //应用业务日志 打印警告日志
        if (e instanceof Exception) {
            LoggerUtil.warn(loggerCallBack, CommLoggerFactory.formatLog("分发消息_应用业务警告日志_" + msg.getTag(), " abstractNotifyCallBack consume message failed by business messageId:,tag:,exMsg:",
                    msg.getMsgID(), msg.getTag(), exMsg));
            return;
        }
        //系统应用日志 打印错误日志
        LoggerUtil.error(loggerCallBack, CommLoggerFactory.formatLog("分发消息_应用系统错误日志_" + msg.getTag(), " abstractNotifyCallBack consume message failed by system messageId:,tag:,exMsg:",
                msg.getMsgID(), msg.getTag(), exMsg));
        return;
    }

}
