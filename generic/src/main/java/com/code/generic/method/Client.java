package com.code.generic.method;

/**
 * @author shunhua
 * @date 2019-10-22
 */
public class Client {

    public static void main(String[] args) {

        // 正常的情况下
        String str = GenericUtil.returnT("hello world");
        System.out.println(str);

        int num = GenericUtil.returnT(Integer.parseInt("123"));
        System.out.println(num);

        User user = GenericUtil.returnT(new User("小明", 18));
        System.out.println(user);

        // 泛型- 直接类型不匹配
        // Long result = GenericUtil.returnGenerator("123");

        // 编译时报错
        Long result = GenericUtil.returnT("123");

    }
}
