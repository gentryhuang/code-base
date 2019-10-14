package com.code.async.message.client.util;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class NoopSampler implements Sampler {
    @Override
    public boolean isSampled() {
        return false;
    }
}
