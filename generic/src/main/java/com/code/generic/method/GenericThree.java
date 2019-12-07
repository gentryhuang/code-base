package com.code.generic.method;

/**
 * GenericThree
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/12/06
 * <p>
 * desc： 在使用泛型的时候，我们还可以为传入的泛型类型实参进行上下边界的限制，如：类型实参只准传入某种类型的父类或某种类型的子类。
 */
public class GenericThree<T extends Number> {

    private T key;

    public void setKey(T key) {
        this.key = key;
    }

    public T getKey() {
        return key;
    }


    /**
     *
     * 在泛型方法中添加上下边界限制的时候，必须在权限声明与返回值之间的<T>上添加上下边界，即在泛型声明的时候添加。泛型的上下边界添加必须与泛型的声明在一起
     *
     * @param contain
     * @param <T>
     * @return
     */
    public <T extends Number> T shouwKey(GenericOne.Generic<T> contain) {
        T test = contain.getKey();
        return test;
    }

}
