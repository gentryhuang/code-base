package com.code.common.logger;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logger工厂
 *
 * @author shunhua
 * @date 2019-09-25
 */
public class CommLoggerFactory {
    /**
     * Service层异常错误日志记录
     */
    public final static Logger EXCEPTION_HANDLER_LOGGER = LoggerFactory.getLogger("EXCEPTION_HANDLER");
    /**
     * 超时日志
     */
    public final static Logger TIME_OUT_LOGGER = LoggerFactory.getLogger("TIME_OUT");
    /**
     * 服务监控日志
     */
    public final static Logger TIME_MONITOR_LOGGER = LoggerFactory.getLogger("TIME_MONITOR");
    /**
     * 定时任务日志
     */
    public final static Logger JOB_LOGGER = LoggerFactory.getLogger("JOB");
    /**
     * 消息日志
     */
    public final static Logger ROCKET_MQ_LOGGER = LoggerFactory.getLogger("ROCKETMQ");
    /**
     * 业务日志
     */
    public final static Logger BUSINESS_LOGGER = LoggerFactory.getLogger("BUSINESS");

    /**
     * MQ消息日志
     */
    public final static Logger MESSAGE = LoggerFactory.getLogger("MESSAGE");

    /**
     * solr 数据日志
     */
    public final static Logger SOLR_LOGGER = LoggerFactory.getLogger("SOLR");
    /**
     * 线程日志
     */
    public final static Logger THREAD_POOL_LOGGER = LoggerFactory.getLogger("THREAD_POOL");
    /**
     * 异步任务日志
     */
    public final static Logger ASYAN_TASK_LOGGER = LoggerFactory.getLogger("ASYAN_TASK");

    /**
     * logger格式化成 alert monitor 格式,如果是字符串,直接输出,是对象,输出JSON格式
     *
     * @param title   the title
     * @param message the message
     * @return string string
     */
    public static String formatLog(String title, Object... message) {
        StringBuilder sb = new StringBuilder();
        // alert monitor 格式
        if (StringUtils.isNotBlank(title)) {
            sb.append("【").append(title).append("】。");
        }
        if (!ArrayUtils.isEmpty(message)) {
            sb.append(JSON.toJSONString(message));
        }
        return sb.toString();
    }
}
