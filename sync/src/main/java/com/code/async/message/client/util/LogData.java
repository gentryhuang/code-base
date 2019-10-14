package com.code.async.message.client.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class LogData {

    private long timestamp;

    private Map<String, ?> fields = new HashMap<>();

    public LogData(long timestamp, String message, Object payload) {
        this.timestamp = timestamp;
        Map<String, Object> tmpFields = new HashMap<>();
        if (message != null && message.trim().length() > 0) {
            tmpFields.put("event", message);
        }
        if (payload != null) {
            tmpFields.put("payload", payload);
        }
        this.fields = Collections.unmodifiableMap(tmpFields);
    }

    public LogData(long timestamp, Map<String, ?> fields) {
        this.timestamp = timestamp;
        this.fields = Collections.unmodifiableMap(fields);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Map<String, ?> getFields() {
        return fields;
    }


}
