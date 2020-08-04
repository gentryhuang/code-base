package com.alibaba.study.rpc.provider;

import com.alibaba.study.rpc.framework.RpcFramework;
import com.alibaba.study.rpc.service.HelloService;
import com.alibaba.study.rpc.service.impl.HelloServiceImpl;

/**
 * RpcProvider
 */
public class RpcProvider {
    public static void main(String[] args) throws Exception {
        // 服务实现
        HelloService service = new HelloServiceImpl();
        // 暴露服务
        RpcFramework.export(service, 1234);
    }
}
