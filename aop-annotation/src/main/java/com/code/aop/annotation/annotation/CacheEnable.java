package com.code.aop.annotation.annotation;

import com.code.aop.annotation.constant.CacheKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CacheEnable
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/10/31
 * <p>
 * desc：
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheEnable {

    CacheKey getCacheKeyFromParam();

    /**
     * 存储缓存的key，备注：getCacheComplexKey里的key必存
     *
     * @return
     */
    CacheKey[] putCacheComplexKeyFromReturn() default {};


    Class clazz();

}
