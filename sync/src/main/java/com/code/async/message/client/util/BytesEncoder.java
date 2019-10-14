package com.code.async.message.client.util;

import io.opentracing.Span;

import java.util.List;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public interface BytesEncoder {

    int LENGTH = 4;

    byte[] encode(Span input);

    byte[] encodeList(List<byte[]> inputs);

}
