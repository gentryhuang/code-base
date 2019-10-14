package com.code.async.message.client.util;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.Sequencer;
import io.opentracing.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class DisruptorReporter implements Reporter {

    private static final Logger logger = LoggerFactory.getLogger(DisruptorReporter.class);

    private final SizePolicy sizePolicy = new SpanSizePolicy();

    private final Sequence sequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);

    private final RingBuffer<SpanEvent> ringBuffer;

    private final Sender sender;

    private final BytesEncoder bytesEncoder;

    private final ReporterMetric reporterMetric;

    private final long period;

    private final ExecutorService executorService;

    private AtomicBoolean suspend = new AtomicBoolean(false);

    private volatile boolean readyClose;

    /**
     * RingBuffer 大小，必须是 2 的 N 次方；如： 1024 * 1024
     *
     * @param maxSize
     */
    public DisruptorReporter(int maxSize, long period, Sender sender) {
        if (sender == null) {
            sender = new NoopSender();
        }
        this.period = period;
        this.ringBuffer = RingBuffer.createMultiProducer(new SpanEventFactory(), maxSize, new EmptyWaitStrategy());
        this.ringBuffer.addGatingSequences(sequence);
        this.sender = sender;
        this.bytesEncoder = new ProtostuffEncoder();
        this.reporterMetric = InMemoryReporterMetric.me;
        this.executorService = Executors.newSingleThreadExecutor(new ThreadFactoryUtils("span-poller", true));
        this.executorService.submit(new Poller());
    }

    @Override
    public void report(Span span) {
        if (suspend.get()) {
            reporterMetric.incrementSpanDropped();
            return;
        }
        byte[] data = bytesEncoder.encode(span);
        if (data == null) {
            return;
        }
        if (!sizePolicy.isAllow(data)) {
            reporterMetric.incrementSpanExceeded();
            return;
        }
        boolean isSuccess = this.ringBuffer.tryPublishEvent(((event, sequence) -> event.set(data)));
        if (!isSuccess) {
            suspend.set(true);
            reporterMetric.incrementSpanDropped();
        } else {
            reporterMetric.incrementSpan();
        }
    }

    @Override
    public void close() {
        readyClose = true;
        sender.close();
    }

    class Poller implements Runnable {

        private long timestamp;

        public Poller() {
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public void run() {

            while (true) {
                if (readyClose) {
                    break;
                }
                try {
                    if (System.currentTimeMillis() - this.timestamp < period && !suspend.get()) {
                        LockSupport.parkNanos(5 * 1000000);
                        continue;
                    }
                    suspend.set(true);
                    doSend();
                    this.timestamp = System.currentTimeMillis();
                    suspend.set(false);
                } catch (Throwable e) {
                    suspend.set(true);
                    readyClose = true;
                    logger.info("The thread {} is interrupted", Thread.currentThread().getName());
                    break;
                }
            }
        }

        public void doSend() {
            long nextSequence = sequence.get() + 1;
            if (nextSequence > ringBuffer.getCursor()) {
                return;
            }
            int size = 0;
            List<byte[]> datas = new ArrayList<>();
            do {
                SpanEvent spanEvent = ringBuffer.get(nextSequence);
                try {
                    if (spanEvent.get() == null) {
                        continue;
                    }
                    byte[] data = spanEvent.get();
                    size = size + data.length + BytesEncoder.LENGTH;
                    if (size > sender.maxMessageBytes()) {
                        sender.send(bytesEncoder.encodeList(datas));
                        size = data.length + BytesEncoder.LENGTH;
                        datas = new ArrayList<>();
                    }
                    datas.add(data);
                } catch (Exception e) {
                    logger.info("error send", e.getMessage());
                } finally {
                    spanEvent.set(null);
                    sequence.set(nextSequence);
                    nextSequence++;
                }
            } while (nextSequence <= ringBuffer.getCursor());
            if (datas.size() > 0) {
                sender.send(bytesEncoder.encodeList(datas));
            }
        }
    }

}
