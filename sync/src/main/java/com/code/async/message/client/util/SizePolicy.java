package com.code.async.message.client.util;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public interface SizePolicy<T> {

    boolean isAllow(T t);
}
