package com.code.async.message.client.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class ThreadFactoryUtils implements ThreadFactory {

    private final AtomicInteger num = new AtomicInteger(1);

    private final String prefix;

    private final boolean daemon;

    public ThreadFactoryUtils(String prefix, boolean daemon) {
        this.prefix = prefix + "-thread-";
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = prefix + num.getAndIncrement();
        Thread t = new Thread(r, name);
        t.setDaemon(daemon);
        return t;
    }
}
