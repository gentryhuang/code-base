package com.code.async.message.client.util;

import io.opentracing.Span;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class ProtostuffEncoder implements BytesEncoder {

    public final static Schema schema = RuntimeSchema.createFrom(CodeSpan.class);

    private final static ThreadLocal<LinkedBuffer> bufThreadLocal = new ThreadLocal<LinkedBuffer>() {

        @Override
        protected LinkedBuffer initialValue() {
            return LinkedBuffer.allocate();
        }
    };

    @Override
    public byte[] encode(Span input) {
        if(input==null||!(input instanceof CodeSpan)){
            return null;
        }
        LinkedBuffer buf = bufThreadLocal.get();
        try {
            return ProtostuffIOUtil.toByteArray(input, schema, buf);
        } finally {
            buf.clear();
        }
    }

    @Override
    public byte[] encodeList(List<byte[]> inputs) {
        int sizeOfArray = 0;
        int length = inputs.size();
        for (int i = 0; i < length; ) {
            sizeOfArray += inputs.get(i++).length;
            sizeOfArray = sizeOfArray + LENGTH;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(sizeOfArray);
        for (int i = 0; i < length; ) {
            byte[] v = inputs.get(i++);
            byteBuffer.putInt(v.length);
            byteBuffer.put(v);
        }
        return byteBuffer.array();
    }
}
