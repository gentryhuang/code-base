package com.code.async.message.client.util;

import com.lmax.disruptor.EventFactory;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class SpanEventFactory implements EventFactory<SpanEvent> {
    @Override
    public SpanEvent newInstance() {
        return new SpanEvent();
    }
}
