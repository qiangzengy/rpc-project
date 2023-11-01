package com.qiangzengy.rpc.provider.common.server.base;

import com.qiangzengy.rpc.codec.RpcDecoder;
import com.qiangzengy.rpc.codec.RpcEncoder;
import com.qiangzengy.rpc.provider.common.handler.RpcProviderHandler;
import com.qiangzengy.rpc.provider.common.server.api.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiangzengy@gmail.com
 * @date 2023/5/23
 */
public class BaseServer implements Server {

    private final Logger logger = LoggerFactory.getLogger(BaseServer.class);

    protected String host = "127.0.0.1";

    protected int port = 27110;

    protected Map<String,Object> handlerMap = new HashMap<>();

    public BaseServer(String serverAddress) {
        if (StringUtils.isNotEmpty(serverAddress)){
            String[] split = serverAddress.split(":");
            this.host = split[0];
            this.port = Integer.parseInt(split[1]);
        }
    }

    /**
     * 启动和配置服务器，接受传入的连接并处理网络请求
     *
     * 下面是使用 ServerBootstrap 的一般步骤：
     *
     * 创建 EventLoopGroup：EventLoopGroup 是处理事件循环的线程池，通常包括两个实例，一个用于处理连接请求（bossGroup），另一个用于处理实际的网络 I/O（workerGroup）。
     *
     * 创建 ServerBootstrap 实例：通过 new ServerBootstrap() 创建一个新的 ServerBootstrap 对象。
     *
     * 配置 ServerBootstrap：使用链式调用的方式配置 ServerBootstrap。一些常用的配置包括设置 EventLoopGroup、指定服务器通道类型、设置服务器的选项和属性等。
     *
     * 指定服务器的处理器：通过调用 handler() 方法来指定服务器通道的处理器，处理器负责处理接收到的请求。
     *
     * 绑定端口并启动服务器：使用 bind() 方法来绑定服务器的监听端口，并启动服务器。可以通过调用 sync() 方法来阻塞等待服务器启动完成。
     *
     * closeFuture().sync() 用于阻塞等待服务器关闭
     */
    @Override
    public void startNettyServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    // 指定服务器通道类型
                    .channel(NioServerSocketChannel.class)
                    // 指定处理接收到的请求的处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
//                                    // 解码，将接收到的字节数据解码为字符串,netty 自带的编解码
//                                    .addLast(new StringDecoder())
//                                    // 编码，将字符串编码为字节数据，netty 自带的编解码
//                                    .addLast(new StringEncoder())
                                    // 自定义的解码
                                    .addLast(new RpcDecoder())
                                    // 自定义的编码
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcProviderHandler(handlerMap))
                            ;
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                            .childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            logger.info("Server started on {}:{}",host,port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
