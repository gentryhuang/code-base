package com.code.aop.annotation.util;

import com.code.common.logger.CommLoggerFactory;
import com.code.common.logger.CommLoggerMarkers;
import com.code.common.logger.LoggerUtil;

import java.lang.reflect.Field;

/**
 * ReflectUtil
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/11/01
 * <p>
 * desc：
 */
public class ReflectUtil {

    /**
     * 利用反射机制，根据属性名获取属性值
     *
     * @param fieldName
     * @param object
     * @return
     */
    public static Object getFieldValueByFieldName(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问（表示反射时不检查属性权限）
            //field.setAccessible(true) 只是改变了field实例的属性，没有改变object实例属性的权限
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException fe) {
            try {
                // 从父类取
                Field field = object.getClass().getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException fe2) {

            } catch (IllegalAccessException e) {
                LoggerUtil.error(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS,e);
            }
        } catch (Exception e) {
            LoggerUtil.error(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS,e);
        }
        return null;
    }

    /**
     * 是否包含属性
     *
     * @param fieldName
     * @param object
     * @return
     */
    public static boolean isHaveFiled(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            if (null != field) {
                return true;
            }
        } catch (NoSuchFieldException e) {
        } catch (Exception e) {
            LoggerUtil.error(CommLoggerFactory.BUSINESS_LOGGER, CommLoggerMarkers.BUSINESS,e);
        }
        return false;
    }
}
