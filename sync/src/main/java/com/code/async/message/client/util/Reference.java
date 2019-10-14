package com.code.async.message.client.util;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class Reference {

    private final String type;

    private final CodeSpanContext spanContext;

    public Reference(String type, CodeSpanContext dfireSpanContext) {
        this.type = type;
        this.spanContext = dfireSpanContext;
    }

    public String getType() {
        return type;
    }

    public CodeSpanContext getDfireSpanContext() {
        return spanContext;
    }
}
