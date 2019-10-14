package com.code.async.message.client.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class Ids {

    private static final String IP_HEX = Integer.toHexString(NetUtils.getIpv4());
    private static final String THREAD_LOCAL_RANDOM_CLASS_NAME =
            "java.util.concurrent.ThreadLocalRandom";
    private static final ThreadLocal<Random> threadLocal =
            new ThreadLocal<Random>() {
                @Override
                protected Random initialValue() {
                    return new Random();
                }
            };
    private static boolean threadLocalRandomPresent = true;

    static {
        try {
            Class.forName(THREAD_LOCAL_RANDOM_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            threadLocalRandomPresent = false;
        }
    }

    public static String uniqueId() {
        long val = 0;
        while (val == 0) {
            val = current().nextLong();
        }
        return IP_HEX + val;
    }

    public static String spanId() {
        long val = 0;
        while (val == 0) {
            val = current().nextLong();
        }
        return String.valueOf(Math.abs(val));
    }

    private static Random current() {
        if (threadLocalRandomPresent) {
            return ThreadLocalRandomAccessor.getCurrentThreadLocalRandom();
        } else {
            return threadLocal.get();
        }
    }


    private static class ThreadLocalRandomAccessor {
        private static Random getCurrentThreadLocalRandom() {
            return ThreadLocalRandom.current();
        }
    }
}
