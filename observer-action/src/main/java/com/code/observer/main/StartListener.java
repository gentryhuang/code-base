package com.code.observer.main;

import org.springframework.stereotype.Service;

/**
 * @author shunhua
 * @date 2019-10-11
 */
@Service
public class StartListener extends AbstractListener {


    @Override
    protected void actionChange() {
        System.out.println("业务处理开始...start");
    }

    @Override
    public Event.Type getEventType() {
        return Event.Type.START;
    }
}
