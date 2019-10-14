package com.code.async.message.client.util;

import io.opentracing.SpanContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class CodeSpanContext implements SpanContext {

    private final String traceId;

    private final String spanId;

    private final String parentId;

    private final String serviceName;

    private final String parentName;
    private final Map<String, String> baggage;
    private boolean isSampler;

    public CodeSpanContext(String traceId, String spanId, String parentId, String serviceName, String parentName, boolean isSampler) {
        this(traceId, spanId, parentId, serviceName, parentName, isSampler, Collections.<String, String>emptyMap());
    }

    public CodeSpanContext(String traceId, String spanId, String parentId, String serviceName, String parentName, boolean isSampler, Map<String, String> baggage) {
        this.parentName = parentName;
        if (baggage == null) {
            throw new NullPointerException();
        }
        this.isSampler = isSampler;
        this.traceId = traceId;
        this.spanId = spanId;
        this.parentId = parentId;
        this.serviceName = serviceName;
        this.baggage = baggage;
    }

    @Override
    public Iterable<Map.Entry<String, String>> baggageItems() {
        return new HashMap(baggage).entrySet();
    }

    public String getBaggageItem(String key) {
        return this.baggage.get(key);
    }

    public boolean isSampler() {
        return isSampler;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getParentName() {
        return parentName;
    }

    public String getParentId() {
        return parentId;
    }

    public CodeSpanContext withBaggageItem(String key, String val) {
        this.baggage.put(key, val);
        return this;
    }

    public CodeSpanContext withSampler(boolean isSampler) {
        this.isSampler = isSampler;
        return this;
    }

    public CodeSpanContext withBaggage(Map<String, String> baggage) {
        this.baggage.putAll(baggage);
        return this;
    }


}
