package com.code.async.message.client.util;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public interface ReporterMetric {

    void incrementSpan();

    void incrementSpanDropped();

    void incrementSpanExceeded();

    long span();

    long spanDropped();

    long spanExceeded();

}
