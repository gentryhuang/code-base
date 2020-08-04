package com.alibaba.study.rpc.service.impl;

import com.alibaba.study.rpc.service.HelloService;

/**
 * HelloServiceImpl
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello " + name;
    }
}
