package com.qiangzengy.rpc.consumer.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.qiangzengy.rpc.consumer.common.context.RpcContext;
import com.qiangzengy.rpc.consumer.common.future.RpcFuture;
import com.qiangzengy.rpc.protocol.RpcProtocol;
import com.qiangzengy.rpc.protocol.header.RpcHeader;
import com.qiangzengy.rpc.protocol.request.RpcRequest;
import com.qiangzengy.rpc.protocol.response.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiangzeng
 * @date 2023/11/8
 */
public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private static Logger logger = LoggerFactory.getLogger(RpcConsumerHandler.class);

    private volatile Channel channel;

    private SocketAddress remoteAddress;

    /**
     * 存储请求ID和RpcResponse协议对象的映射关系
     * 在channelRead0进行数据赋值
     */
    //private Map<Long, RpcProtocol<RpcResponse>> pendingResponse = new ConcurrentHashMap<>();

    private Map<Long, RpcProtocol<RpcResponse>> pendingResponse = new ConcurrentHashMap<>();

    private Map<Long, RpcFuture> pendingRpc = new ConcurrentHashMap<>();


    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * 在Netty激活链接时，对remoteAddress进行赋值
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remoteAddress = this.channel.remoteAddress();
    }

    /**
     * 在Netty注册链接时，对channel进行赋值
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> rpcResponseRpcProtocol) throws Exception {
        RpcHeader header = rpcResponseRpcProtocol.getHeader();
        long requestId = header.getRequestId();
        logger.info("消费者接收的数据：{}", JSONObject.toJSONString(rpcResponseRpcProtocol));
        RpcFuture rpcFuture = pendingRpc.remove(requestId);
        // 设置数据到rpcFuture
        if (rpcFuture != null){
            rpcFuture.done(rpcResponseRpcProtocol);
        }

       // pendingResponse.put(requestId,rpcResponseRpcProtocol);


    }

    /**
     * 服务消费者向服务提供者发送数据
     * @param protocol
     */
    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol){
        logger.info("服务消费者发送的数据：{}",JSONObject.toJSONString(protocol));
//        RpcHeader header = protocol.getHeader();
//        long requestId = header.getRequestId();
//        while (true){
//            RpcProtocol<RpcResponse> responseRpcProtocol = pendingResponse.remove(requestId);
//            if (responseRpcProtocol != null){
//                return responseRpcProtocol.getBody().getResult();
//            }
//        }
//        RpcFuture rpcFuture = getRpcFuture(protocol);
//        channel.writeAndFlush(protocol);
        RpcRequest body = protocol.getBody();
        boolean oneway = body.isOneway();
        boolean async = body.isAsync();
        if (oneway){
            return sendRequestOneway(protocol);
        }else if(async){
            return sendRequestAsync(protocol);
        }else {
            return sendRequestSync(protocol);
        }
    }

    /**
     * 同步
     * @param protocol
     * @return
     */
    private RpcFuture sendRequestSync(RpcProtocol<RpcRequest> protocol){
        RpcFuture rpcFuture = this.getRpcFuture(protocol);
        channel.writeAndFlush(protocol);
        return rpcFuture;
    }

    /**
     * 异步
     * @param protocol
     */
    private RpcFuture sendRequestAsync(RpcProtocol<RpcRequest> protocol){
        RpcFuture rpcFuture = this.getRpcFuture(protocol);
        RpcContext.getContext().setRpcFuture(rpcFuture);
        channel.writeAndFlush(protocol);
        return rpcFuture;
    }

    /**
     * 单向
     * @param protocol
     */
    private RpcFuture sendRequestOneway(RpcProtocol<RpcRequest> protocol){
        channel.writeAndFlush(protocol);
        return null;
    }


    private RpcFuture getRpcFuture(RpcProtocol<RpcRequest> protocol){
        RpcFuture rpcFuture = new RpcFuture(protocol);
        RpcHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        pendingRpc.put(requestId,rpcFuture);
        return rpcFuture;
    }

    /**
     * 关闭来链接
     */
    public void close(){
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }
}
