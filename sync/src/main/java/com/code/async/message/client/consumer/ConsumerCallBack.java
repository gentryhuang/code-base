package com.code.async.message.client.consumer;

import com.code.async.message.client.model.AsyncMsg;

/**
 *
 * 消费类接口,实现该类处理具体的业务类型
 *
 * 例子: DoBusiness类只处理messageTagA和messageTagB的消息</p>
 *
 * @MessageTag(tag = {"messageTagA","messageTagB"})
 * public class DoBusiness implements ConsumerCallBack {
 *      @Override
 *      public boolean process(AsyncMsgTO msgTO) {
 *          //do business
 *          return false;
 *      }
 * }
 *
 * @author shunhua
 * @date 2019-10-14
 */
public interface ConsumerCallBack {

    /**
     * 业务具体处理方法
     *
     * @return
     */
    boolean process(AsyncMsg msg);
}
