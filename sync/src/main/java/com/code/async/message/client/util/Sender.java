package com.code.async.message.client.util;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public interface Sender {

    void send(byte[] datas);

    int maxMessageBytes();

    void close();

}
