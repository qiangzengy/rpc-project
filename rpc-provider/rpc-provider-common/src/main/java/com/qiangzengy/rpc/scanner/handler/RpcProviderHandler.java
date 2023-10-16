package com.qiangzengy.rpc.scanner.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author qiangzengy@gmail.com
 * @date 2023/5/23
 *  实现消息的收发功能，继承SimpleChannelInboundHandler
 *
 *  我们创建了一个自定义的 RpcProviderHandler 类，继承自 SimpleChannelInboundHandler<Object>。我们指定了消息类型为 Object，
 *  因此 channelRead0() 方法的参数类型也为 Object。
 *
 * 在 channelRead0() 方法中，我们处理接收到的消息，并在控制台打印消息内容。然后，我们构造了一个响应消息，并通过 ctx.writeAndFlush() 方法将响应消息发送给客户端。
 *
 * 需要注意的是，在 SimpleChannelInboundHandler 中，不需要手动释放消息资源。Netty 会在处理完毕后自动释放消息，避免了内存泄漏的风险。
 *
 * 另外，SimpleChannelInboundHandler 还提供了 exceptionCaught() 方法，用于处理异常情况。在这个方法中，我们可以定义自己的异常处理逻辑，例如打印异常信息并关闭连接。
 *
 * 总结而言，SimpleChannelInboundHandler 是 Netty 提供的用于处理入站消息的抽象类，它简化了消息处理的逻辑，并提供了自动释放资源的功能，使开发者能够更专注于业务逻辑的实现。
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        // 处理接收到的消息
        System.out.println("Received message: " + o);

        // 响应客户端，直接返回数据
        ctx.writeAndFlush(0);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常处理
        cause.printStackTrace();
        ctx.close();
    }

}
