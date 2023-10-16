package com.qiangzengy.rpc.test.scanner.consumer.service.impl;

import com.qiangzengy.rpc.annotation.RpcReference;
import com.qiangzengy.rpc.test.scanner.consumer.service.ConsumerBusinessService;
import com.qiangzengy.rpc.test.scanner.provider.service.DemoService;

/**
 * @author qiangzeng
 * @date 2023/10/10
 */
public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {

    @RpcReference(group ="qiangzeng")
    private DemoService demoService;

    public void sayHello(){
        demoService.sayHello();
    }


}
