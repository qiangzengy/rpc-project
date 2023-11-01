package com.qiangzengy.rpc.consumer.codec;

import com.qiangzengy.rpc.consumer.codec.init.RpcTestConsumerInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author qiangzeng
 * @date 2023/11/1
 */
public class RpcTestConsumer {

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventExecutors = new NioEventLoopGroup(4);
        try {
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new RpcTestConsumerInitializer());
            bootstrap.connect("127.0.0.1", 27880)
                    .sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            Thread.sleep(10000);
            eventExecutors.shutdownGracefully();
        }

    }
}
