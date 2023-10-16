package com.qiangzengy.rpc.test.scanner.provider.service.impl;

import com.qiangzengy.rpc.annotation.RpcService;
import com.qiangzengy.rpc.test.scanner.provider.service.DemoService;

@RpcService(interfaceClass = DemoService.class, interClassName =
        "com.qiangzengy.rpc.test.scanner.service.DemoService", version = "1.0.0",
        group = "qiangzeng")
public class DemoServiceImpl implements DemoService {

    @Override
    public void sayHello() {
        System.out.println("Hello World");
    }
}
