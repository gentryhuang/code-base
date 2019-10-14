package com.code.async.message.client.util;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class SystemClock implements Clock {
    private final long createTimestamp;

    private final long createTick;

    public SystemClock() {
        createTimestamp = System.currentTimeMillis() * 1000;
        createTick = System.nanoTime();
    }

    @Override
    public long currentTimeMicros() {
        return (System.nanoTime() - createTick) / 1000 + createTimestamp;
    }
}
