package com.code.aop.annotation.aop;

import com.code.aop.annotation.annotation.CacheConfig;
import com.code.aop.annotation.annotation.CacheEvict;
import com.code.aop.annotation.constant.CacheKey;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * CacheEvictAspect
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/10/31
 * <p>
 * desc：
 */
@Aspect
@Component
public class CacheEvictAspect implements ApplicationContextAware {

    private ApplicationContext ctx;


    @AfterReturning(pointcut = "@annotation(com.code.aop.annotation.annotation.CacheEvict)", returning = "o")
    @AfterThrowing(pointcut = "@annotation(com.code.aop.annotation.annotation.CacheEvict)", throwing = "ex")
    public void after(JoinPoint joinPoint, Object o) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        CacheEvict memberCacheEvict = method.getAnnotation(CacheEvict.class);
        CacheConfig memberCacheConfig = joinPoint.getTarget().getClass().getAnnotation(CacheConfig.class);


        String directory = memberCacheConfig.directory();
        CacheKey[] cacheComplexKeys = memberCacheEvict.cacheEvictKeys();

        String cacheServiceName = memberCacheConfig.cacheServiceName();

        ICacheService cacheService = (ICacheService) ctx.getBean(cacheServiceName);

        LoggerUtil.log(Level.INFO, MemberCenterLoggerFactory.CACHE_LOGGER, McLoggerMarker.CACHE,
                new JsonKvFormat("进入CacheEnableAspect after ++++++++++++")
                        .add("cacheComplexKeys", cacheComplexKeys)
                        .add("directory", directory));

        if (null == cacheService) {
            LoggerUtil.log(Level.ERROR, MemberCenterLoggerFactory.CACHE_LOGGER, McLoggerMarker.CACHE,
                    new JsonKvFormat("未初始化cacheService bean").add("cacheComplexKeys", cacheComplexKeys)
                            .add("directory", directory));
        }
        // 缓存的key
        List<String> cacheKeys = CacheEnableAspect.parsingKeyFromParam(directory, cacheComplexKeys, joinPoint);
        //  key为空不走缓存
        if (CollectionUtils.isEmpty(cacheKeys)) {
            return;
        }

        String[] cacheKeysOnDirectory = new String[cacheKeys.size()];
        for (int i = 0; i < cacheKeys.size(); i++) {
            cacheKeysOnDirectory[i] = cacheKeys.get(i);
        }

        long result = cacheService.del(cacheKeysOnDirectory);
        if (result <= 0) {
            LoggerUtil.log(Level.WARN, MemberCenterLoggerFactory.CACHE_LOGGER, McLoggerMarker.CACHE,
                    new JsonKvFormat("缓存删除失败").add("cacheKeysOnDirectory", cacheKeysOnDirectory)
                            .add("directory", directory)
                            .add("value", JSON.toJSONString(joinPoint.getTarget())));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }


    public static Method findMethod(Class clazz, String name) {
        return findMethod(clazz, name, new Class[0]);
    }

    public static Method findMethod(Class clazz, String name, Class[] paramTypes) {
        Class searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (name.equals(method.getName()) &&
                        (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }
}