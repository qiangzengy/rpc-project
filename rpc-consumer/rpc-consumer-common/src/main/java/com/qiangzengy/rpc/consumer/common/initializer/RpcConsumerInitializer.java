package com.qiangzengy.rpc.consumer.common.initializer;

import com.qiangzengy.rpc.codec.RpcDecoder;
import com.qiangzengy.rpc.codec.RpcEncoder;
import com.qiangzengy.rpc.consumer.common.handler.RpcConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author qiangzeng
 * @date 2023/11/9
 */
public class RpcConsumerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(new RpcEncoder());
        channelPipeline.addLast(new RpcDecoder());
        channelPipeline.addLast(new RpcConsumerHandler());
    }
}
