package com.code.async.message.client.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shunhua
 * @date 2019-10-14
 */
public class NumberUtils {

    private static final Pattern PATTERN_NUMBER = Pattern.compile("^\\d*[.]?\\d*$");

    /**
     * 是否是数字.
     *
     * @param source 源数字.
     * @return true, 是正常数字.
     */
    public static boolean isNumber(String source) {
        if (source == null || source.length() == 0) {
            return true;
        }
        Matcher m = PATTERN_NUMBER.matcher(source);
        if (!m.find()) {
            return false;
        }
        String first = null;
        int split = source.indexOf('.');
        if (split == -1) {
            first = source;
        } else {
            first = source.substring(0, split);
        }
        if (first == null || first.length() == 0) {
            return true;
        }
        if (first.length() > 1 && first.startsWith("0")) {
            return false;
        }
        return true;
    }

}
