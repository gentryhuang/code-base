package com.code.async.message.client.util;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class PrefixedKeys {

    public static String prefixedKey(String key, String prefix) {
        return prefix + key;
    }

    public static String unprefixedKey(String key, String prefix) {
        return key.substring(prefix.length());
    }

}
