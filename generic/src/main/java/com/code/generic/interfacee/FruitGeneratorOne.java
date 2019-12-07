package com.code.generic.interfacee;

/**
 * FruitGenerator
 *
 * @author <a href="mailto:libao.huang@yunhutech.com">shunhua</a>
 * @since 2019/12/06
 * <p>
 * desc：
 *  1. 定义类实现泛型接口时，当未给范型接口传入泛型实参时必须将泛型的声明一起加到类上,即代码中显示的情况
 *  2. 如果不声明泛型，如：class FruitGenerator implements Generator<T>，编译器会报错："Unknown class"
 *
 */
public class FruitGeneratorOne<T> implements Generator<T> {

    @Override
    public T next() {
        return null;
    }
}
