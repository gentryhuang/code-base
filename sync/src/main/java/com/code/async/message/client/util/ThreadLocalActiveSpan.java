package com.code.async.message.client.util;

import io.opentracing.ActiveSpan;
import io.opentracing.Span;
import io.opentracing.SpanContext;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class ThreadLocalActiveSpan implements ActiveSpan {

    private final ThreadLocalActiveSpanSource source;
    private final Span wrapped;
    private final ThreadLocalActiveSpan toRestore;
    private final AtomicInteger refCount;

    ThreadLocalActiveSpan(ThreadLocalActiveSpanSource source, Span wrapped, AtomicInteger refCount) {
        this.source = source;
        this.refCount = refCount;
        this.wrapped = wrapped;
        this.toRestore = source.tlsSnapshot.get();
        source.tlsSnapshot.set(this);
    }

    @Override
    public void deactivate() {
        if (source.tlsSnapshot.get() != this) {
            return;
        }
        source.tlsSnapshot.set(toRestore);

        if (0 == refCount.decrementAndGet()) {
            wrapped.finish();
        }
    }

    @Override
    public Continuation capture() {
        return new ThreadLocalActiveSpan.Continuation();
    }

    @Override
    public SpanContext context() {
        return wrapped.context();
    }

    @Override
    public ThreadLocalActiveSpan setTag(String key, String value) {
        wrapped.setTag(key, value);
        return this;
    }

    @Override
    public ThreadLocalActiveSpan setTag(String key, boolean value) {
        wrapped.setTag(key, value);
        return this;
    }

    @Override
    public ThreadLocalActiveSpan setTag(String key, Number value) {
        wrapped.setTag(key, value);
        return this;
    }

    @Override
    public ThreadLocalActiveSpan log(Map<String, ?> fields) {
        wrapped.log(fields);
        return this;
    }

    @Override
    public ThreadLocalActiveSpan log(long timestampMicroseconds, Map<String, ?> fields) {
        wrapped.log(timestampMicroseconds, fields);
        return this;
    }

    @Override
    public ThreadLocalActiveSpan log(String event) {
        wrapped.log(event);
        return this;
    }

    @Override
    public ThreadLocalActiveSpan log(long timestampMicroseconds, String event) {
        wrapped.log(timestampMicroseconds, event);
        return this;
    }

    @Override
    public ThreadLocalActiveSpan setBaggageItem(String key, String value) {
        wrapped.setBaggageItem(key, value);
        return this;
    }

    @Override
    public String getBaggageItem(String key) {
        return wrapped.getBaggageItem(key);
    }

    @Override
    public ThreadLocalActiveSpan setOperationName(String operationName) {
        wrapped.setOperationName(operationName);
        return this;
    }

    @Override
    public ThreadLocalActiveSpan log(String eventName, Object payload) {
        wrapped.log(eventName, payload);
        return this;
    }

    @Override
    public ThreadLocalActiveSpan log(long timestampMicroseconds, String eventName, Object payload) {
        wrapped.log(timestampMicroseconds, eventName, payload);
        return this;
    }

    @Override
    public void close() {
        deactivate();
    }

    private final class Continuation implements ActiveSpan.Continuation {
        Continuation() {
            refCount.incrementAndGet();
        }

        @Override
        public ThreadLocalActiveSpan activate() {
            return new ThreadLocalActiveSpan(source, wrapped, refCount);
        }
    }

}
