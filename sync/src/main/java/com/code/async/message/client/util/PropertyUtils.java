package com.code.async.message.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author shunhua
 * @date 2019-10-14 10:54
 */
public class PropertyUtils {

    private static final Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

    public static String stringOrDefault(String value, String defaultValue) {
        return !StringUtils.isEmpty(value) ? value : defaultValue;
    }

    public static Number numberOrDefault(Number value, Number defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static Integer getPropertyAsInt(String name) {
        String value = getProperty(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.error("Failed to parse integer for property '" + name + "' with value '" + value + "'", e);
            }
        }
        return null;
    }

    public static Long getPropertyAsLong(String name) {
        String value = getProperty(name);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                logger.error("Failed to parse Long for property '" + name + "' with value '" + value + "'", e);
            }
        }
        return null;
    }

    public static String getProperty(String name) {
        return System.getProperty(name, System.getenv(name));
    }

}
