package com.code.common.exception;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author shunhua
 * @date 2019-09-25
 */
public class ExceptionPicker {
    /**
     * 错误信息提取方法
     *
     * @param e - 异常信息
     * @return 可输出的日志
     */
    public static String pickup(Throwable e) {
        try {
            return handleException(e, null);
        } catch (Exception e1) {
            return e1.getMessage();
        }
    }

    /**
     * 错误信息提取方法
     *
     * @param request 请求信息
     * @param e       - 异常信息
     * @return 可输出的日志
     */
    public static String pickup(HttpServletRequest request, Throwable e) {
        try {
            return handleException(e, request);
        } catch (Exception e1) {
            return e1.getMessage();
        }
    }

    /**
     * 处理异常
     *
     * @param e - 异常信息
     * @return 可输出的日志
     */
    private static String handleException(Throwable e, HttpServletRequest request) {
        String message = e.getMessage();
        if (StringUtils.isBlank(message)) {
            return assembleMsg(message, e);
        }
        if (message.startsWith(ExceptionMessage.DUBBO_PREFIX)) {
            // DUBBO异常：日志处理机制
            return assembleMsg(dubboPrefix(message), e);
        }
        if (message.indexOf(ExceptionMessage.DUBBO_TIMEOUT) > 0) {
            // 超时异常：日志处理机制
            return assembleMsg(dubboTimeOut(message), e);
        }
        if (message.contains(ExceptionMessage.REQUIRED_PARAMETER)) {
            // 没有请求参数异常，例如：Required String parameter 'seat_code' is not present
            return assembleMsg(requireParam(message, request), e);
        }
        // 其它异常：输出当前工程package下对应的错误栈信息
        return assembleMsg(message, e);
    }

    /**
     * DUBBO异常：日志处理机制
     *
     * @param message the message
     * @return the sorted message
     */
    private static String dubboPrefix(String message) {
        String className = StringUtils.EMPTY;
        String method = StringUtils.EMPTY;
        StringBuilder builder = new StringBuilder();
        int beginIndex = ExceptionMessage.DUBBO_PREFIX.length();
        int endIndex = message.indexOf(ExceptionMessage.DUBBO_IN_SERVICE);
        if (endIndex > 0) {
            method = message.substring(beginIndex, endIndex);
        }
        beginIndex = endIndex + 15;
        endIndex = message.indexOf(ExceptionMessage.DUBBO_DOT, beginIndex);
        if (endIndex > 0) {
            className = message.substring(beginIndex, endIndex + 1);
        }
        if (!(StringUtils.EMPTY + className).endsWith(ExceptionMessage.DOT)) {
            className = className + ExceptionMessage.DOT;
        }
        beginIndex = message.indexOf(ExceptionMessage.DUBBO_TIMEOUT_COLON);
        if (beginIndex > 0) {
            endIndex = message.indexOf(ExceptionMessage.DUBBO_MS_REQUEST);
            if (endIndex > 0) {
                String timeout = message.substring(beginIndex, endIndex + 4);
                builder.append(ExceptionMessage.EXCEPTION_DUBBO).append(timeout).append(className).append(method);
                builder.append(ExceptionMessage.COLON).append(StringUtils.substringAfterLast(message, ExceptionMessage.DUBBO_CHANNEL));
                return builder.toString();
            } else {
                return message;
            }
        } else {
            beginIndex = message.indexOf(ExceptionMessage.DUBBO_NO_PROVIDER);
            if (beginIndex > 0) {
                return builder.append(ExceptionMessage.DUBBO_NO_EXIST).append(className).append(method).toString();
            } else {
                return message;
            }
        }
    }

    /**
     * 超时异常：日志处理机制
     *
     * @param message the message
     * @return the sorted message
     */
    private static String dubboTimeOut(String message) {
        StringBuilder builder = new StringBuilder();
        String className = StringUtils.substringBetween(message, ExceptionMessage.EQ_INTERFACE, ExceptionMessage.DUBBO_SEMICOLON);
        String method = StringUtils.substringBetween(message, ExceptionMessage.EQ_METHODNAME, ExceptionMessage.DUBBO_SEMICOLON);
        String timeout = StringUtils.substringBetween(message, ExceptionMessage.EQ_TIMEOUT, ExceptionMessage.DUBBO_SEMICOLON);
        if (StringUtils.length(timeout) > 10) {
            timeout = StringUtils.substringBetween(message, ExceptionMessage.EQ_TIMEOUT, ExceptionMessage.DUBBO_AMP);
        }
        if (!(StringUtils.EMPTY + className).endsWith(ExceptionMessage.DOT)) {
            className = className + ExceptionMessage.DOT;
        }
        builder.append(ExceptionMessage.EXCEPTION_DUBBO).append(ExceptionMessage.DUBBO_TIMEOUT).append(ExceptionMessage.SPACE);
        builder.append(timeout).append(ExceptionMessage.DUBBO_MS).append(className).append(method);
        builder.append(ExceptionMessage.COLON).append(StringUtils.substringAfterLast(message, ExceptionMessage.DUBBO_CHANNEL));
        return builder.toString();
    }

    /**
     * 没有请求参数异常，例如：Required String parameter 'seat_code' is not present
     *
     * @param message the message
     * @param request the http servlet request
     * @return the sorted message
     */
    private static String requireParam(String message, HttpServletRequest request) {
        if (request == null) {
            return message;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(message).append(ExceptionMessage.SPLIT);
        String requestMethod = (request.getMethod() + StringUtils.EMPTY).trim().toUpperCase(); // POST/GET 1.去空格, 2.转大写 字母
        StringBuilder urlParams = new StringBuilder();
        urlParams.append(requestMethod).append(ExceptionMessage.SPACE);
        urlParams.append(request.getRequestURI()).append(ExceptionMessage.QUESTION).append(request.getQueryString());
        if (ExceptionMessage.POST.equals(requestMethod)) {
            if (StringUtils.isNotBlank(request.getQueryString())) { // POST 请求时，URL上包含参数
                urlParams.append(ExceptionMessage.DUBBO_AMP);
            }
            Map<String, String[]> params = request.getParameterMap(); // 以下是POST参数组装
            for (String key : params.keySet()) {
                urlParams.append(key).append(ExceptionMessage.EQUALS);
                String[] values = params.get(key);
                int length = values.length;
                for (int i = 0; i < length; i++) {
                    urlParams.append(values[i]);
                    if (i + 1 < length) { // 判断是否最后一个参数
                        urlParams.append(ExceptionMessage.DUBBO_AMP);
                    }
                }
            }
        }
        return builder.append(urlParams.toString()).toString();
    }

    /**
     * 其它异常：输出当前工程package下对应的错误栈信息
     *
     * @param message the message
     * @param e       the exception
     * @return the sorted message
     */
    private static String assembleMsg(String message, Throwable e) {
        return e.getClass().getCanonicalName() + ExceptionMessage.COLON + message + getExceptionCauseLine(e);
    }

    /**
     * 获取异常调用栈信息
     *
     * @param e the exception
     * @return the cause line of exception
     */
    private static String getExceptionCauseLine(Throwable e) {
        StringBuilder builder = new StringBuilder();
        // 遍历异常调用栈
        for (StackTraceElement element : e.getStackTrace()) {
            // 不是指定的包名，就进行异常信息的拼接
            if (isCompanyPackage(element.getClassName())) {
                builder.append(ExceptionMessage.EXCEPTION_GT_GT).append(element.toString()).append(System.lineSeparator());
            }
        }
        return System.lineSeparator() + builder.toString();
    }

    /**
     * 排除指定包名(排除动态代理、过滤器、切面)
     *
     * @param name 包名
     * @return true-是, false-否
     */
    private static boolean isCompanyPackage(String name) {
        return !StringUtils.isBlank(name)
                && !name.contains(ExceptionMessage.CGLIB)
                && !name.contains(ExceptionMessage.DO_FILTER)
                && !name.contains(ExceptionMessage.ASPECT_AROUND);
    }
}
