package com.code.async.message.client.util;

import com.code.async.message.client.constants.Config;
import com.code.async.message.client.constants.Constants;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class CodeSpan implements Span {
    private String traceId;

    private String id;

    private String parentId;

    private String parentName;

    private Long startTimestamp;

    private transient CodeSpanContext spanContext;

    private transient Reporter reporter;

    private transient Clock clock;

    private String operationName;

    private Long duration;

    private Map<String, Object> tags = new ConcurrentHashMap<>();

    private List<LogData> logs = new ArrayList<>();

    private transient volatile boolean isFinish = false;

    public CodeSpan(){}

    public CodeSpan(
            String traceId,
            String id,
            String parentId,
            String parentName,
            String operationName,
            Long startTimestamp,
            CodeSpanContext spanContext,
            Reporter reporter,
            Clock clock
    ){
        this.traceId = traceId;
        this.id = id;
        this.parentId = parentId;
        this.parentName = parentName;
        this.operationName = operationName;
        this.spanContext = spanContext;
        this.reporter = reporter;
        this.clock = clock;
        if (startTimestamp != null) {
            this.startTimestamp = startTimestamp;
        } else {
            this.startTimestamp = clock.currentTimeMicros();
        }
    }

    @Override
    public void finish() {
        finish(clock.currentTimeMicros());
    }

    @Override
    public void finish(long finishMicros) {
        if (isFinish) {
            return;
        }
        isFinish = true;
        this.duration = finishMicros - startTimestamp;
        if (spanContext.isSampler()) {
            reporter.report(this);
        }
    }

    @Override
    public CodeSpanContext context() {
        return this.spanContext;
    }

    @Override
    public io.opentracing.Span setTag(String key, String value) {
        return setTagAsObject(key, value);
    }

    @Override
    public io.opentracing.Span setTag(String key, boolean value) {
        return setTagAsObject(key, value);
    }

    @Override
    public io.opentracing.Span setTag(String key, Number value) {
        return setTagAsObject(key, value);
    }

    @Override
    public synchronized io.opentracing.Span log(Map<String, ?> fields) {
        return log(clock.currentTimeMicros(), fields);
    }

    @Override
    public synchronized io.opentracing.Span log(long timestampMicroseconds, Map<String, ?> fields) {
        logs.add(new LogData(timestampMicroseconds, fields));
        return this;
    }

    @Override
    public synchronized io.opentracing.Span log(String event) {
        return log(clock.currentTimeMicros(), event);
    }

    @Override
    public synchronized io.opentracing.Span log(long timestampMicroseconds, String event) {
        logs.add(new LogData(timestampMicroseconds, event, null));
        return this;
    }

    @Override
    public synchronized io.opentracing.Span setBaggageItem(String key, String value) {
        this.spanContext.withBaggageItem(key, value);
        return this;
    }

    @Override
    public synchronized String getBaggageItem(String key) {
        return this.spanContext.getBaggageItem(key);
    }

    @Override
    public io.opentracing.Span setOperationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    /**
     * @deprecated use {@link #log(Map)} like this
     * {@code span.log(Map.of("event", "timeout"))}
     * or
     * {@code span.log(timestampMicroseconds, Map.of("event", "exception", "payload", stackTrace))}
     **/
    @Override
    @Deprecated
    public synchronized io.opentracing.Span log(String eventName, Object payload) {
        return log(clock.currentTimeMicros(), eventName, payload);
    }

    /**
     * @deprecated use {@link #log(Map)} like this
     * {@code span.log(timestampMicroseconds, Map.of("event", "timeout"))}
     * or
     * {@code span.log(timestampMicroseconds, Map.of("event", "exception", "payload", stackTrace))}
     **/
    @Override
    @Deprecated
    public synchronized io.opentracing.Span log(long timestampMicroseconds, String eventName, Object payload) {
        logs.add(new LogData(timestampMicroseconds, eventName, payload));
        return this;
    }

    private CodeSpan setTagAsObject(String key, Object value) {
        if (StringUtils.isEmpty(key) || value == null) {
            return this;
        }
        if (key.equals(Tags.SAMPLING_PRIORITY.getKey()) && (value instanceof Number)) {
            int priority = ((Number) value).intValue();
            if (priority > 0) {
                spanContext.withSampler(true);
            }
        }

        if (spanContext.isSampler()) {
            tags.put(key, value);
        }

        return this;
    }

    public boolean isError() {
        Object status = getTag(Constants.RESPONSE_STATUS);
        return Config.STATUS.ERR.getType().equals(status);
    }

    public String getTraceId() {
        return traceId;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public String getOperationName() {
        return operationName;
    }

    public Long getDuration() {
        return duration;
    }

    public <T> T getTag(String key) {
        if (key == null) {
            return null;
        }
        Object value = tags.get(key);
        if (value == null) {
            return null;
        }
        return (T) value;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    public List<LogData> getLogs() {
        return logs;
    }


}
