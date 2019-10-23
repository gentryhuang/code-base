package com.code.generic.method;

/**
 * @author shunhua
 * @date 2019-10-22
 */
public class GenericUtil {
    /**
     * 泛型方法，返回的值类型取决方法的接收类型,避免了手动显示转换。接收类型和传入参数类型不匹配就会报转换异常的错误
     *
     * @param o   这里o类型是Object，换成泛型T最好
     * @param <T> 泛型
     * @return
     */
    public static <T> T returnT(Object o) {
        return (T)o;
    }

    /**
     * 返回类型是Object类型的方法，具体返回值取决与传入的参数类型，需要手动显示转换
     *
     * @param o
     * @return
     */
    public static Object returnObject(Object o) {
        return o;
    }
}
