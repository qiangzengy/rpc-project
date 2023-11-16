package com.qiangzengy.rpc.test.consumer.handler;

import com.qiangzengy.rpc.consumer.common.RpcConsumer;
import com.qiangzengy.rpc.consumer.common.future.RpcFuture;
import com.qiangzengy.rpc.protocol.RpcProtocol;
import com.qiangzengy.rpc.protocol.enums.RpcType;
import com.qiangzengy.rpc.protocol.header.RpcHeaderFactory;
import com.qiangzengy.rpc.protocol.request.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * @author qiangzeng
 * @date 2023/11/9
 */
public class RpcConsumerHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(RpcConsumerHandlerTest.class);

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        RpcConsumer consumer = RpcConsumer.getInstance();
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk", RpcType.REQUEST.getType()));
        RpcRequest request = new RpcRequest();
        request.setClassName("com.qiangzengy.rpc.test.api.DemoService");
        request.setGroup("qiangzeng");
        request.setMethodName("sayHell");
        request.setParameters(new Object[]{"qiangzeng"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(true);
        request.setOneway(false);
        protocol.setBody(request);
        RpcFuture rpcFuture = consumer.sendRequest(protocol);
        logger.info("从服务消费者获取到的数据===>>{}",rpcFuture.get());
        consumer.close();

    }

}
