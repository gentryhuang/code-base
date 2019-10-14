package com.code.async.message.client.util;

import io.opentracing.Span;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public interface Reporter {

    void report(Span span);

    void close();

}
