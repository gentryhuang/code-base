package com.code.async.message.client.util;

import io.opentracing.Span;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class NoopReporter implements Reporter {

    @Override
    public void report(Span span) {

    }

    @Override
    public void close() {

    }
}
