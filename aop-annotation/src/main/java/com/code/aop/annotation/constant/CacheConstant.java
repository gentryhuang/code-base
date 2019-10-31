package com.code.aop.annotation.constant;

/**
 * CacheConstant
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/10/31
 * <p>
 * desc：
 */
public class CacheConstant {
    /**
     * 缓存的key
     */
    interface Cachekey {

    }

    /**
     * 缓存时间
     */
    interface CacheExpireTime {
        /**
         * 一分钟
         */
        static final int EXPIRE_TWO_SECOND = 2;
        /**
         * 一分钟
         */
        static final int EXPIRE_MINUTE = 60;

        /**
         * 两分钟
         */
        static final int EXPIRE_TWO_MINUTE = 120;

        /**
         * 5分钟
         */
        static final int EXPIRE_FIVE_MINUTE = 300;

        /**
         * 10分钟
         */
        static final int EXPIRE_TEN_MINUTE = 600;

        /**
         * 1小时
         */
        static final int EXPIRE_HOUR = 60 * 60;

        /**
         * 一天
         */
        static final int EXPIRE_DAY = 24 * 60 * 60;

        /**
         * 一周
         */
        static final int EXPIRE_WEEK = 24 * 60 * 60 * 7;
    }
}
