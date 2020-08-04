package com.alibaba.study.rpc.consumer;

import com.alibaba.study.rpc.framework.RpcFramework;
import com.alibaba.study.rpc.service.HelloService;

/**
 * RpcConsumer
 */
public class RpcConsumer {

    public static void main(String[] args) throws Exception {
        // 引用服务【代理对象】
        HelloService service = RpcFramework.refer(HelloService.class, "127.0.0.1", 1234);
        while (true) {
            String hello = service.hello("World");
            System.out.println(hello);
            Thread.sleep(1000);
        }
    }

}
