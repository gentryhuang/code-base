package com.code.observer.main;

import org.springframework.stereotype.Service;

/**
 * @author shunhua
 * @date 2019-10-11
 */
@Service
public class EndListener extends AbstractListener {

    /**
     * 事件处理
     */
    @Override
    protected void actionChange() {
        System.out.println("业务处理结束...end");
    }

    /**
     * EndListener 监听器对应的事件类型
     *
     * @return
     */
    @Override
    public Event.Type getEventType() {
        return Event.Type.END;
    }
}
