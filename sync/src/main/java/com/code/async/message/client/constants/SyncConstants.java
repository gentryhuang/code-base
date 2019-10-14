package com.code.async.message.client.constants;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class SyncConstants {

    /**
     * 默认常量值
     */
    public static class Default {

        public static final String SUCCESS = "request success";
        public static final String FAIL = "request fail";
        public static final int TEN = 10;
        public static final int FIFTEEN = 15;
        public static final String DEFAULT_USERNAME = "用户";
        public static final int THOUSAND = 1000;
        public static final int TEN_THOUSAND = TEN * THOUSAND;
        //一分钟(ms)
        public static final long ONE_MINUTE_MS = 60 * 1000L;
        public static final int FOUR_HOUR_MS = 4 * 60 * 60 * 1000;
        public static final Long TWO_MONTH_MS = 60 * 24 * 60 * 60 * 1000L;

        /**
         * 三小时，单位：毫秒
         */
        public static final Long THREE_HOUR_MS = 3 * 60 * 60 * 1000L;

    }

    /**
     * mq tag
     */
    public static class Tag{
        public static final String BUSINESS_TAG = "bussiness_tag";
    }


    /**
     * 缓存key
     */
    public static class CacheKey {
        //重复消息
        public static final String REPEAT_CONSUMER_MQ_MSG_KEY =  "repeat_consumer_mq_msg:";
    }

    /**
     * 缓存时间
     */
    public static class CacheTime {

        //7天(m)
        public static final int SEVEN_DAY_S = 7 * 24 * 60 * 60;
        //一天(ms)
        public static final long ONE_DAY_MS = 24 * 60 * 60 * 1000;
        //一天(s)
        public static final int ONE_DAY_S = 24 * 60 * 60;
        //2天（s）
        public static final int TWO_DAY_S = 2 * 24 * 60 * 60;
        //5秒
        public static final int FIVE_SECOND = 5;
        //1小时
        public static final int ONE_HOUR = 60 * 60;
        //2小时
        public static final int TWO_HOUR = 2 * 60 * 60;
        //消息过期
        public static final Long MSG_EXPIRE_INTERVAL_TIME = 1000L * 5 * 60;
        //一分钟（S）
        public static final int ONE_MINUTE_S = 60;
        //1秒
        public static final int ONE_SECONDS = 1;

        public static final int FOUR_HOUR_SECONDS = 4 * 60 * 60;
    }


}
