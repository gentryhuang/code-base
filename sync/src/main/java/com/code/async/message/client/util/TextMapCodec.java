package com.code.async.message.client.util;

import io.opentracing.propagation.TextMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shunhua
 * @date 2019-10-14 11:48
 */
public class TextMapCodec implements Injector<TextMap>, Extractor<TextMap> {

    public static final String SPAN_CONTEXT_KEY = "trace-id";
    public static final char SPAN_CONTEXT_SEPARATE = ':';
    public static final String BAGGAGE_KEY_PREFIX = "tx-";
    private static final Logger logger = LoggerFactory.getLogger(TextMapCodec.class);
    private final boolean urlEncoding;

    public TextMapCodec(boolean urlEncoding) {
        this.urlEncoding = urlEncoding;
    }


    @Override
    public void inject(CodeSpanContext spanContext, TextMap carrier) {
        carrier.put(SPAN_CONTEXT_KEY, encodedValue(contextAsString(spanContext)));
        for (Map.Entry<String, String> entry : spanContext.baggageItems()) {
            carrier.put(PrefixedKeys.prefixedKey(entry.getKey(), BAGGAGE_KEY_PREFIX), encodedValue(entry.getValue()));
        }
    }

    @Override
    public CodeSpanContext extract(TextMap carrier) {
        CodeSpanContext context = null;
        Map<String, String> baggage = null;
        for (Map.Entry<String, String> entry : carrier) {
            String key = entry.getKey().toLowerCase();
            if (key.equals(SPAN_CONTEXT_KEY)) {
                context = contextFromString(decodedValue(entry.getValue()));
            } else if (key.startsWith(BAGGAGE_KEY_PREFIX)) {
                if (baggage == null) {
                    baggage = new HashMap<>();
                }
                baggage.put(PrefixedKeys.unprefixedKey(key, BAGGAGE_KEY_PREFIX), decodedValue(entry.getValue()));
            }
        }
        if (baggage == null) {
            return context;
        }
        return context.withBaggage(baggage);
    }

    private String contextAsString(CodeSpanContext spanContext) {
        return StringUtils.join(SPAN_CONTEXT_SEPARATE,
                spanContext.getTraceId(),
                spanContext.getSpanId(),
                spanContext.getServiceName(),
                spanContext.getParentName(),
                spanContext.isSampler());
    }


    private CodeSpanContext contextFromString(String value) {
        if (value == null || value.equals("")) {
            return null;
        }

        String[] parts = value.split(":");
        if (parts.length != 5) {
            logger.error("String does not match tracer state format:{}", value);
            return null;
        }
        return new CodeSpanContext(
                parts[0],
                parts[1],
                null,
                parts[2],
                parts[3],
                Boolean.valueOf(parts[4]));
    }


    private String encodedValue(String value) {
        if (!urlEncoding) {
            return value;
        }
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private String decodedValue(String value) {
        if (!urlEncoding) {
            return value;
        }
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

}
