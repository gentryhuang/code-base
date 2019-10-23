package com.code.generic.method;

/**
 * @author shunhua
 * @date 2019-10-22
 */
public class Client {

    public static void main(String[] args) {
        String str = GenericUtil.returnT("hello world");
        System.out.println(str);

        int num = GenericUtil.returnT(Integer.parseInt("123"));
        System.out.println(num);

        User user = GenericUtil.returnT(new User("小明", 18));
        System.out.println(user);
    }
}
