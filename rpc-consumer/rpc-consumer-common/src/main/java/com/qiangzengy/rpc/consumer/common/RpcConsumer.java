package com.qiangzengy.rpc.consumer.common;

import com.qiangzengy.rpc.consumer.common.future.RpcFuture;
import com.qiangzengy.rpc.consumer.common.handler.RpcConsumerHandler;
import com.qiangzengy.rpc.consumer.common.initializer.RpcConsumerInitializer;
import com.qiangzengy.rpc.protocol.RpcProtocol;
import com.qiangzengy.rpc.protocol.request.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiangzeng
 * @date 2023/11/9
 */
public class RpcConsumer {

    private final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventExecutors;

    private static volatile RpcConsumer instance;

    private static Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();

    private RpcConsumer(){
        this.bootstrap = new Bootstrap();
        this.eventExecutors = new NioEventLoopGroup(4);
        this.bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new RpcConsumerInitializer());
    }

    /**
     * 单例，双重检查
     * @return RpcConsumer
     */
    public static RpcConsumer getInstance(){
        if (instance == null){
            synchronized (RpcConsumer.class){
                if (instance == null){
                    instance = new RpcConsumer();
                }
            }
        }
        return instance;
    }

    public void close(){
        eventExecutors.shutdownGracefully();
    }

    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol) throws InterruptedException {
        // TODO serviceAddress、port 可以从注册中心拿取
        String serviceAddress = "127.0.0.1";
        int port = 27880;
        String key = serviceAddress.concat("_").concat(String.valueOf(port));
        RpcConsumerHandler rpcConsumerHandler = handlerMap.get(key);
        if (rpcConsumerHandler == null){
            rpcConsumerHandler = getRpcConsumerHandler(serviceAddress, port);
            handlerMap.put(key,rpcConsumerHandler);
        }else if(!rpcConsumerHandler.getChannel().isActive()){ // 缓存中存在RpcConsumerHandler，但不是活跃
            rpcConsumerHandler.close();
            rpcConsumerHandler = getRpcConsumerHandler(serviceAddress,port);
            handlerMap.put(key,rpcConsumerHandler);
        }
        return rpcConsumerHandler.sendRequest(protocol);
    }

    public RpcConsumerHandler getRpcConsumerHandler(String serviceAddress, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap
                .connect(serviceAddress, port)
                .sync();
        channelFuture.addListener((ChannelFutureListener) listener -> {
            if (channelFuture.isSuccess()){
                logger.info("connect rpc server {} on port {} success", serviceAddress, port);
            }else {
                logger.error("connect rpc server {} on port {} failed", serviceAddress, port);
                channelFuture.cause().printStackTrace();
                close();
            }

        });
        return channelFuture
                .channel()
                .pipeline()
                .get(RpcConsumerHandler.class);
    }



}
