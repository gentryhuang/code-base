package com.code.common.logger;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Logger的Marker
 * @author shunhua
 * @date 2019-09-25
 */
public class CommLoggerMarkers {
    /**
     * 超时警告
     */
    public static final Marker TIME_OUT_WARM = MarkerFactory.getMarker("time_out");
    /**
     * 异常处理
     */
    public static final Marker EXCEPTION_HANDLER = MarkerFactory.getMarker("exception_handler");
    /**
     * 业务marker
     */
    public static final Marker BUSINESS = MarkerFactory.getMarker("business");

    /**
     * 数据埋点
     */
    public static final Marker STATISTIC = MarkerFactory.getMarker("statistic");

    /**
     * solr marker
     */
    public static final Marker SOLR = MarkerFactory.getMarker("solr");


    /**
     * 定时任务
     */
    public static final Marker JOB = MarkerFactory.getMarker("job");

    /**
     * mq消息marker
     */
    public static final Marker ROCKET_MQ = MarkerFactory.getMarker("rocket_mq");

    /**
     * 线程池marker
     */
    public static final Marker THREAD_POOL = MarkerFactory.getMarker("threadPool");

    /**
     * xxx marker
     */
    public static final Marker XXX = MarkerFactory.getMarker("xxx");

}
