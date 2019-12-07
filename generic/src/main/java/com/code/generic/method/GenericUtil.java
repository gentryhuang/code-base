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
        return (T) o;
    }

    /**
     * 纯泛型
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T returnGenerator(T t) {
        return t;
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


    /**
     *
     *  说明：
     *       1）public 与 返回值中间<T>非常重要，可以理解为声明此方法为泛型方法。
     *       2）只有声明了<T>的方法才是泛型方法，泛型类中的使用了泛型的成员方法并不是泛型方法。
     *       3）<T>表明该方法将使用泛型类型T，此时才可以在方法中(入参、出参以及方法体内)使用泛型类型T。
     *       4）与泛型类的定义一样，此处T可以随便写为任意标识，常见的如T、E、K、V等形式的参数常用于表示泛型。
     *
     * @param clazz
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <T> T genericMethod(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T t = clazz.newInstance();
        return t;
    }
}
