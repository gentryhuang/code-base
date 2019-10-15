package com.code.async.message.client.useconsumer;

import com.code.async.message.client.constants.SyncConstants;
import com.code.async.message.client.consumer.annotation.MessageTag;
import com.code.async.message.client.consumer.base.AbstractNotifyCallBack;
import com.code.async.message.client.model.AsyncMsg;
import org.springframework.stereotype.Component;

/**
 * @author shunhua
 * @date 2019-10-15
 */
@Component
@MessageTag(tag = {SyncConstants.Tag.BUSINESS_TAG})
public class CancleHandler extends AbstractNotifyCallBack {

    @Override
    protected boolean handle(AsyncMsg msg) throws Exception {
        System.out.println("msg:" + msg);
        return false;
    }
}
