package com.code.common.logger;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shunhua
 * @date 2019-09-25
 */
public class LogFactory {

    //----------------------------------------------------应用级日志----------------------------------------------------
    /**
     * 超时日志
     */
    public final static Logger TIME_OUT_LOGGER = LoggerFactory.getLogger("TIME_OUT");
    /**
     * 异常Handler日志
     */
    public final static Logger EXCEPTION_HANDLER_LOGGER = LoggerFactory.getLogger("EXCEPTION_HANDLER");

    //----------------------------------------------------业务日志----------------------------------------------------
    /**
     * 业务日志
     */
    public final static Logger BUSINESS_LOGGER = LoggerFactory.getLogger("BUSINESS");

    /**
     * 根据logger名获取指定的Logger
     *
     * @param loggerName the logger name
     * @return the Logger Instance
     */
    public static Logger getLogger(String loggerName) {
        if (StringUtils.isEmpty(loggerName)) {
            return BUSINESS_LOGGER;
        }
        return LoggerFactory.getLogger(loggerName);
    }
}
