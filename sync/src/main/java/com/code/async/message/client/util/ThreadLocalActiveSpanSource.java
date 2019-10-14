package com.code.async.message.client.util;

import io.opentracing.ActiveSpan;
import io.opentracing.ActiveSpanSource;
import io.opentracing.Span;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class ThreadLocalActiveSpanSource implements ActiveSpanSource {

    final ThreadLocal<ThreadLocalActiveSpan> tlsSnapshot = new ThreadLocal<ThreadLocalActiveSpan>();

    @Override
    public ThreadLocalActiveSpan activeSpan() {
        return tlsSnapshot.get();
    }

    @Override
    public ActiveSpan makeActive(Span span) {
        return new ThreadLocalActiveSpan(this, span, new AtomicInteger(1));
    }


}
