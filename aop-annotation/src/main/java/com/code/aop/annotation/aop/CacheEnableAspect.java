package com.code.aop.annotation.aop;

import com.alibaba.fastjson.JSON;
import com.code.aop.annotation.annotation.CacheConfig;
import com.code.aop.annotation.annotation.CacheEnable;
import com.code.aop.annotation.constant.CacheConstant;
import com.code.aop.annotation.constant.CacheKey;
import com.code.aop.annotation.util.ReflectUtil;
import com.code.cache.jedis.cache.ICacheService;
import com.code.common.logger.CommLoggerFactory;
import com.code.common.logger.CommLoggerMarkers;
import com.code.common.logger.LoggerUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * CacheEnableAspect
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/10/31
 * <p>
 * desc：
 */
@Aspect
@Component
public class CacheEnableAspect implements ApplicationContextAware {
    private ApplicationContext ctx;

    @Around("@annotation(com.code.aop.annotation.annotation.CacheEnable)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        CacheEnable memberCacheEnable = method.getAnnotation(CacheEnable.class);
        CacheConfig memberCacheConfig = joinPoint.getTarget().getClass().getAnnotation(CacheConfig.class);


        String cacheServiceName = memberCacheConfig.cacheServiceName();
        ICacheService cacheService = (ICacheService) ctx.getBean(cacheServiceName);

        String directory = memberCacheConfig.directory();
        CacheKey cacheComplexKey = memberCacheEnable.getCacheKeyFromParam();

        LoggerUtil.info(CommLoggerFactory.BUSINESS_LOGGER,CommLoggerMarkers.BUSINESS,String.format(
                "CacheEnableAspect start =======,cacheComplexKey:%s,directory:%s,clazz:%s",
                String.valueOf(cacheComplexKey),directory,memberCacheEnable.clazz().getSimpleName()
        ));


        if (null == cacheService) {
            LoggerUtil.error(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS,
                    String.format("未初始化cacheService bean"));
            return joinPoint.proceed();
        }
        // key为空不走缓存
        if (null == cacheComplexKey) {
            return joinPoint.proceed();
        }

        // 缓存的key
        List<String> getCacheKey = parsingKeyFromParam(directory, new CacheKey[]{cacheComplexKey}, joinPoint);
        //  key为空不走缓存
        if (CollectionUtils.isEmpty(getCacheKey)) {
            return joinPoint.proceed();
        }

        String result = cacheService.get(directory + ":" + getCacheKey.get(0));
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, memberCacheEnable.clazz());
        }
        Object o = joinPoint.proceed();
        // 放缓存
        cacheService.set(getCacheKey.get(0), JSON.toJSONString(o), cacheComplexKey.getExpirationTime());
        List<String> putCacheKeys = parsingKeyFromReturn(directory, memberCacheEnable.putCacheComplexKeyFromReturn(), o);
        if (!CollectionUtils.isEmpty(putCacheKeys)) {
            putCacheKeys.forEach(putCacheKey -> cacheService.set(putCacheKey, JSON.toJSONString(o), CacheConstant.CacheExpireTime.EXPIRE_WEEK));
        }
        return o;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }


    /**
     * 获取方法参数名称
     *
     * @param joinPoint
     * @return
     */
    private static String[] getFieldsName(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        // 通过这获取到方法的所有参数名称的字符串数组
        String[] parameterNames = methodSignature.getParameterNames();
        return parameterNames;
    }


    /**
     * 解析缓存key
     *
     * @param directory
     * @param cacheComplexKeys
     * @param joinPoint
     * @return
     */
    public static List<String> parsingKeyFromParam(String directory, CacheKey[] cacheComplexKeys, JoinPoint joinPoint) {

        // 从参数中取
        Object[] paramValues = joinPoint.getArgs();

        String[] paramNames = getFieldsName(joinPoint);

        LoggerUtil.info(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS,
                String.format("CacheEnableAspect params ======= ,keyExpression:%s,paramNames:%s,paramValues:%s",
                        String.valueOf(cacheComplexKeys),String.valueOf(paramNames),String.valueOf(paramValues)));


        Map<String, Object> paramsMap = new HashMap<>(paramValues.length);
        for (int i = 0; i < paramNames.length; i++) {
            paramsMap.put(paramNames[i], paramValues[i]);
        }
        //  解析key
        List<String> cacheKeys = new ArrayList<>(cacheComplexKeys.length);
        Arrays.stream(cacheComplexKeys).forEach(cacheComplexKey -> {
            String[] keyExpressionValues = new String[cacheComplexKey.getKeyReplaceValues().length];

            boolean keyExpressionValuesIsNull = true;
            for (int i = 0; i < cacheComplexKey.getKeyReplaceValues().length; i++) {
                String keyReplaceValue = cacheComplexKey.getKeyReplaceValues()[i];
                if (keyReplaceValue.startsWith("#")) {
                    String keyReplaceValueOfKey = keyReplaceValue.replace("#", "");
                    if (!keyReplaceValueOfKey.contains(".")) {
                        keyReplaceValue = paramsMap.get(keyReplaceValueOfKey).toString();
                    } else {
                        keyReplaceValue = parsingValueFromParamObject(keyReplaceValueOfKey, paramsMap);
                    }
                }
                if (StringUtils.isNotBlank(keyReplaceValue)) {
                    // 不需要从参数中取值的话就直接放入结果
                    keyExpressionValues[i] = keyReplaceValue;
                    keyExpressionValuesIsNull = false;
                }
            }
            if (!keyExpressionValuesIsNull) {
                cacheKeys.add(directory + ":" + String.format(cacheComplexKey.getKeyExpression(), keyExpressionValues));
            }
        });
        return cacheKeys;
    }

    public static List<String> parsingKeyFromReturn(String directory, CacheKey[] cacheComplexKeys, Object returnValue) {

        //  解析key
        List<String> cacheKeys = new ArrayList<>(cacheComplexKeys.length);
        Arrays.stream(cacheComplexKeys).forEach(cacheComplexKey -> {
            String[] keyExpressionValues = new String[cacheComplexKeys.length];

            for (int i = 0; i < cacheComplexKey.getKeyReplaceValues().length; i++) {
                String keyReplaceValue = cacheComplexKey.getKeyReplaceValues()[i];
                if (keyReplaceValue.startsWith("#")) {
                    String keyReplaceValueOfKey = keyReplaceValue.replace("#", "");
                    Object value;
                    if (!keyReplaceValueOfKey.contains(".")) {
                        value = ReflectUtil.getFieldValueByFieldName(keyReplaceValueOfKey, returnValue);
                    } else {
                        value = ReflectUtil.getFieldValueByFieldName(keyReplaceValueOfKey.split("\\.")[1], returnValue);
                    }
                    if (null != value) {
                        keyReplaceValue = value.toString();
                    }
                }
                // 不需要从参数中取值的话就直接放入结果
                keyExpressionValues[i] = keyReplaceValue;
            }
            cacheKeys.add(directory + ":" + String.format(cacheComplexKey.getKeyExpression(), keyExpressionValues));
        });
        return cacheKeys;
    }


    private static String parsingValueFromParamObject(String keyName, Map<String, Object> paramsMap) {
        String[] paramObjectAndAttribute = keyName.split("\\.");
        if (paramObjectAndAttribute.length < 2) {
            return keyName;
        }
        String objectName = paramObjectAndAttribute[0];
        String attribute = paramObjectAndAttribute[1];
        Object o = paramsMap.get(objectName);
        //  反射获取值
        Object value = ReflectUtil.getFieldValueByFieldName(attribute, o);
        if (null == value) {
            // 取不到值就返回keyName
            return StringUtils.EMPTY;
        }
        return value.toString();
    }
}


