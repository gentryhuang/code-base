package com.code.async.message.client.util;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class SpanEvent {

    private byte[] data;

    public void set(byte[] data) {
        this.data =data;
    }

    public byte[] get() {
        return this.data;
    }

}
