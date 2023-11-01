package com.qiangzengy.rpc.consumer.codec.init;

import com.qiangzengy.rpc.codec.RpcDecoder;
import com.qiangzengy.rpc.codec.RpcEncoder;
import com.qiangzengy.rpc.consumer.codec.handler.RpcTestConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qiangzeng
 * @date 2023/11/1
 */
public class RpcTestConsumerInitializer extends ChannelInitializer<SocketChannel> {

    private final Logger logger = LoggerFactory.getLogger(RpcTestConsumerInitializer.class);

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        logger.info("执行initChannel方法=========");
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(new RpcEncoder());
        channelPipeline.addLast(new RpcDecoder());
        channelPipeline.addLast(new RpcTestConsumerHandler());
    }
}
