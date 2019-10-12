package com.code.observer.main;

import org.springframework.stereotype.Service;

/**
 * 初始化监听器 - 初始化策略
 *
 * @author shunhua
 * @date 2019-10-11
 */
@Service
public class InitListener extends AbstractListener {

    /**
     * InitListener 对应的事件处理方法
     */
    @Override
    protected void actionChange() {
        // TODO
        System.out.println("处理业务...init");
    }

    /**
     * InitListener监听器对应的 事件类型
     *
     * @return
     */
    @Override
    public Event.Type getEventType() {
        return Event.Type.INIT;
    }

}
