package com.code.async.message.client.useconsumer;

import com.code.async.message.client.constants.SyncConstants;
import com.code.async.message.client.consumer.ConsumerCallBack;
import com.code.async.message.client.consumer.annotation.MessageTag;
import com.code.async.message.client.model.AsyncMsg;
import org.springframework.stereotype.Component;

/**
 * 可以直接实现ConsumerCallBack接口,但是消息的过滤没有了，最好继承AbstractNotifyCallBack类，它里面对消息进行了过滤
 *
 * @author shunhua
 * @date 2019-10-15
 */
@Component
@MessageTag(tag = {SyncConstants.Tag.BUSINESS_TAG})
public class StartHandler implements ConsumerCallBack {

    @Override
    public boolean process(AsyncMsg msg) {
        System.out.println("StartHandler implements ConsumerCallBack!");
        return true;
    }
}
