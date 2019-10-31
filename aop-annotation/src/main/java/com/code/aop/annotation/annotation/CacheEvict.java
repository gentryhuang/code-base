package com.code.aop.annotation.annotation;

import com.code.aop.annotation.constant.CacheKey;

/**
 * CacheEvict
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/10/31
 * <p>
 * desc：
 */
public @interface CacheEvict {
    CacheKey[] cacheEvictKeys();
}
