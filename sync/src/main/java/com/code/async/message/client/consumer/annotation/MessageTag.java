package com.code.async.message.client.consumer.annotation;

import com.code.async.message.client.consumer.ConsumerCallBack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shunhua
 * @date 2019-10-14
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageTag {
    /**
     * 配置多个Tag,配置方法查看 {@link ConsumerCallBack}
     *
     * @return
     */
    String[] tag() default {};
}
