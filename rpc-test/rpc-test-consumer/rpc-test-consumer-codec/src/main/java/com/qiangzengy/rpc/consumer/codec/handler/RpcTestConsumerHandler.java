package com.qiangzengy.rpc.consumer.codec.handler;

import com.alibaba.fastjson.JSONObject;
import com.qiangzengy.rpc.protocol.RpcProtocol;
import com.qiangzengy.rpc.protocol.enums.RpcType;
import com.qiangzengy.rpc.protocol.header.RpcHeaderFactory;
import com.qiangzengy.rpc.protocol.request.RpcRequest;
import com.qiangzengy.rpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiangzeng
 * @date 2023/11/1
 */
public class RpcTestConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {


   private final Logger logger = LoggerFactory.getLogger(RpcTestConsumerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> rpcResponseRpcProtocol) throws Exception {
        logger.info("消费者接收的数据：{}", JSONObject.toJSONString(rpcResponseRpcProtocol));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("开始发送数据。。。。。");
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk", RpcType.REQUEST.getType()));
        RpcRequest request = new RpcRequest();
        request.setClassName("com.qiangzengy.rpc.test.provider.service.ProviderDemo");
        request.setGroup("qiangzeng");
        request.setMethodName("sayHell");
        request.setParameters(new Object[]{});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);
        protocol.setBody(request);
        ctx.writeAndFlush(protocol);
        logger.info("数据发送结束。。。。。");
    }
}
