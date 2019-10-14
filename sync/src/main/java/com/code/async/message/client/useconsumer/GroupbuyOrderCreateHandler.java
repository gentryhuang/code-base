package com.code.async.message.client.useconsumer;

import com.code.async.message.client.constants.SyncConstants;
import com.code.async.message.client.consumer.base.AbstractNotifyCallBack;
import com.code.async.message.client.consumer.annotation.MessageTag;
import com.code.async.message.client.model.AsyncMsg;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author shunhua
 * @date 2019-10-14 14:43
 */
@Component
@MessageTag(tag = {SyncConstants.Tag.BUSINESS_TAG})
public class GroupbuyOrderCreateHandler extends AbstractNotifyCallBack {

    @Override
    @SuppressWarnings("all")
    public boolean process(AsyncMsg msg) {
        try {
            //过滤消息
            if (filterMsg(msg)) {
                return true;
            }
            if (!handle(msg)) {
                return false;
            }
            //记录消费日志
            // ...
        } catch (Throwable e) {
            handleException(msg, e);
            return true;
        }
        return true;
    }

    @Override
    protected boolean handle(AsyncMsg msg) throws Exception {
        System.out.println("业务处理");
        return true;
    }
}
