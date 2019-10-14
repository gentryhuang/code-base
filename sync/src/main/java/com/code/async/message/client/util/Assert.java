package com.code.async.message.client.util;

import org.springframework.util.StringUtils;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class Assert {

    public static void isNotNull(Object source, String... message) {
        if (source == null) {
            throw new RuntimeException(message[0]);
        }
    }


    public static void isTrue(boolean b, String message) {
        if (!b) {
            throw new RuntimeException(message);
        }
    }

    public static void isNotBlank(String source, String message) {
        if (StringUtils.isEmpty(source)) {
            throw new RuntimeException(message);
        }
    }
}
