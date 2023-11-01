package com.qiangzengy.rpc.test.provider.service.impl;

import com.qiangzengy.rpc.annotation.RpcService;
import com.qiangzengy.rpc.test.provider.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiangzeng
 * @date 2023/10/19
 */

@RpcService(interfaceClass = DemoService.class, interClassName = "com.qiangzengy.rpc.test.provider.service.DemoService", version = "1.0.0", group = "qiangzeng")
public class ProviderDemoServiceImpl implements DemoService {

    private Logger logger = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);

    @Override
    public void sayHell() {
        logger.info("Hello World");
    }
}
