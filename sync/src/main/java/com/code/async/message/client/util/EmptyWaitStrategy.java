package com.code.async.message.client.util;

import com.lmax.disruptor.*;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class EmptyWaitStrategy implements WaitStrategy {
    @Override
    public long waitFor(long sequence, Sequence cursor, Sequence dependentSequence, SequenceBarrier barrier) {
        return Long.MIN_VALUE;
    }

    @Override
    public void signalAllWhenBlocking() {

    }
}
