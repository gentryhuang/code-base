package com.code.async.message.client.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class InMemoryReporterMetric implements ReporterMetric {

    public static final InMemoryReporterMetric me = new InMemoryReporterMetric();

    private final ConcurrentHashMap<MetricKey, LongAdder> metrics = new ConcurrentHashMap<>();

    private InMemoryReporterMetric() {
        metrics.put(MetricKey.span, new LongAdder());
        metrics.put(MetricKey.spanDropped, new LongAdder());
        metrics.put(MetricKey.spanExceeded, new LongAdder());
    }

    @Override
    public void incrementSpan() {
        increment(MetricKey.span);
    }

    @Override
    public void incrementSpanDropped() {
        increment(MetricKey.spanDropped);
    }

    @Override
    public void incrementSpanExceeded() {
        increment(MetricKey.spanExceeded);
    }

    @Override
    public long span() {
        return get(MetricKey.span);
    }

    @Override
    public long spanDropped() {
        return get(MetricKey.spanDropped);
    }

    @Override
    public long spanExceeded() {
        return get(MetricKey.spanExceeded);
    }

    private void increment(MetricKey metricKey) {
        LongAdder metric = metrics.get(metricKey);
        metric.increment();
    }

    private long get(MetricKey metricKey) {
        return metrics.get(metricKey).longValue();
    }

    enum MetricKey {
        span,
        spanDropped,
        spanExceeded,
    }

}
