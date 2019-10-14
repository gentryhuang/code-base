package com.code.async.message.client.util;

import com.code.async.message.client.constants.Config;
import com.code.async.message.client.constants.Constants;
import io.opentracing.BaseSpan;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class ThrowableHandler {

    public static final String TRACE_ID = "traceId";

    public static void handle(BaseSpan baseSpan, Throwable throwable) {
        proccessSpan(baseSpan, throwable);
    }

    private static void proccessSpan(BaseSpan baseSpan, Throwable throwable) {
        baseSpan.setTag(Constants.RESPONSE_STATUS, Config.STATUS.ERR.getType());
        baseSpan.log(throwable.toString());
    }

}
