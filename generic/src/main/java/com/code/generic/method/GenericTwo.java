package com.code.generic.method;

/**
 * GenericTwo
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/12/06
 * <p>
 * desc：
 */
public class GenericTwo<T> {

    /**
     * 泛型类中的成员方法
     *
     * @param t
     */
    public void show_1(T t) {
        System.out.println(t.toString());
    }

    /**
     * 在泛型类中声明了一个泛型方法，使用泛型E，这种泛型E可以为任意类型。可以类型与T相同，也可以不同。
     * 由于泛型方法在声明的时候会声明泛型<E>，因此即使在泛型类中并未声明泛型，编译器也能够正确识别泛型方法中识别的泛型。
     *
     * @param t
     * @param <E>
     */
    public <E> void show_3(E t) {
        System.out.println(t.toString());
    }

    /**
     * 在泛型类中声明了一个泛型方法，使用泛型T，注意这个T是一种全新的类型，可以与泛型类中声明的T不是同一种类型
     *
     * @param t
     * @param <T>
     */
    public <T> void show_2(T t) {
        System.out.println(t.toString());
    }

    /**
     * 在泛型类中声明一个泛型方法，使用泛型T，注意这个T是一种全新的类型，可以与泛型类中声明的T不是同一种类型
     * @param t
     * @param <T>
     * @return
     */
    public <T> T shouw_3(T t) {
        return t;
    }

}
