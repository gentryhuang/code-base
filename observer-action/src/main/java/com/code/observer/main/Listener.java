package com.code.observer.main;

/**
 * 监听器接口
 *
 * @author shunhua
 * @date 2019-10-11
 */
public interface Listener {

    /**
     * 当前监听者处理的事件类型
     *
     * @return
     */
    Event.Type getEventType();

    /**
     * 事件发生回调方法
     */
    void handleEvent(Event event);
}
