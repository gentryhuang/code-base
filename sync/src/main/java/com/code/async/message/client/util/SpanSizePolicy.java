package com.code.async.message.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.code.async.message.client.util.InitFactory.SPAN_MAX_SIZE_KEY;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class SpanSizePolicy implements SizePolicy<byte[]> {

    private static final Logger logger = LoggerFactory.getLogger(SpanSizePolicy.class);

    private static final int DEFAULT_MAX_SPAN_BYTES = 8192;

    private final int maxSpanBytes;

    public SpanSizePolicy() {
        int maxSpanBytes = PropertyUtils.numberOrDefault(
                PropertyUtils.getPropertyAsInt(SPAN_MAX_SIZE_KEY), -1).intValue();
        if (maxSpanBytes <= 0) {
            maxSpanBytes = DEFAULT_MAX_SPAN_BYTES;
        }
        this.maxSpanBytes = maxSpanBytes;
    }

    @Override
    public boolean isAllow(byte[] bytes) {
        boolean isAllow = bytes.length <= maxSpanBytes;
        if (!isAllow) {
            logger.info("span is too large,size:{},maxSize is:{}", bytes.length, maxSpanBytes);
        }
        return isAllow;
    }
}
