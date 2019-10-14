package com.code.async.message.client.util;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class NoopSender implements Sender {
    @Override
    public void send(byte[] datas) {

    }

    @Override
    public int maxMessageBytes() {
        return 0;
    }

    @Override
    public void close() {

    }
}
