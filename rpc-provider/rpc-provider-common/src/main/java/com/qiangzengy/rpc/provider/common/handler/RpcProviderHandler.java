package com.qiangzengy.rpc.provider.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.qiangzengy.rpc.common.helper.RpcServiceHelper;
import com.qiangzengy.rpc.common.threadpool.ServerThreadPool;
import com.qiangzengy.rpc.constants.RpcConstants;
import com.qiangzengy.rpc.protocol.RpcProtocol;
import com.qiangzengy.rpc.protocol.enums.RpcStatus;
import com.qiangzengy.rpc.protocol.enums.RpcType;
import com.qiangzengy.rpc.protocol.header.RpcHeader;
import com.qiangzengy.rpc.protocol.request.RpcRequest;
import com.qiangzengy.rpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author qiangzengy@gmail.com
 * @date 2023/5/23
 *  实现消息的收发功能，继承Netty中的SimpleChannelInboundHandler类
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

public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);


    private final Map<String, Object> handlerMap;

    private final String reflectType;

    /**
     * 用于存储接收到的数据
     * @param handlerMap
     */
//    public RpcProviderHandler(Map<String, Object> handlerMap){
//        this.handlerMap = handlerMap;
//    }


    public RpcProviderHandler(Map<String, Object> handlerMap, String reflectType){
        this.handlerMap = handlerMap;
        this.reflectType = reflectType;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {
        // 处理接收到的消息
        ServerThreadPool.submit(() -> {
            logger.info("RPC提供者收到的数据:{}", JSONObject.toJSONString(protocol));
            RpcHeader header = protocol.getHeader();
            RpcRequest request = protocol.getBody();
            // 将head消息类型设置成响应类型
            header.setMsgType((byte)RpcType.RESPONSE.getType());
            RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
            RpcResponse response = new RpcResponse();
            //
            try {
                Object result = handle(request);
                response.setResult(result);
                response.setAsync(request.isAsync());
                response.setOneway(request.isOneway());
                header.setStatus((byte) RpcStatus.SUCCESS.getCode());
            }catch (Exception e){
                response.setError(e.getMessage());
                header.setStatus((byte) RpcStatus.FAIL.getCode());
                logger.error("RPC Server handle request error",e);
            }
            responseRpcProtocol.setHeader(header);
            responseRpcProtocol.setBody(response);
            // 响应客户端，直接返回数据
            ctx.writeAndFlush(responseRpcProtocol)
                    .addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            logger.debug("Send response for request "+ header.getRequestId());
                        }
                    });
            logger.info("RPC提供者发送响应数据:{}", JSONObject.toJSONString(responseRpcProtocol));
        });

    }

    private Object handle(RpcRequest request) throws Exception {
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object serviceBean = handlerMap.get(serviceKey);
        String methodName = request.getMethodName();
        String className = request.getClassName();

        if (serviceBean == null){
            throw new RuntimeException(String.format("service not exist:%s:%s", className, methodName));
        }
        Class<?> serviceBeanClass = serviceBean.getClass();
        Object[] parameters = request.getParameters();
        Class<?>[] parameterTypes = request.getParameterTypes();
        if (parameters != null && parameters.length > 0){
            for (Object parameter : parameters) {
                logger.debug(parameter.toString());
            }
        }
        if (parameterTypes != null && parameterTypes.length > 0){
            for (Class<?> parameterType : parameterTypes) {
                logger.debug(parameterType.getName());
            }
        }

        switch (this.reflectType){
            case RpcConstants.PROXY_JDK:
                return invokeJdkMethod(serviceBean,serviceBeanClass,methodName,parameterTypes,parameters);
            case RpcConstants.PROXY_CGLIB:
                return invokeCglibMethod(serviceBean,serviceBeanClass,methodName,parameterTypes,parameters);
            default:
                throw new IllegalArgumentException("not support reflect type");
        }
    }

    /**
     * jdk 代理
     * @param serviceBean
     * @param serviceBeanClass
     * @param methodName
     * @param parameterTypes
     * @param parameters
     * @return
     * @throws Exception
     */
    private Object invokeJdkMethod(Object serviceBean, Class<?> serviceBeanClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception {
        logger.info("invokeJdkMethod ...........");
        Method method = serviceBeanClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean,parameters);
    }

    /**
     * cglib 代理
     * @param serviceBean
     * @param serviceBeanClass
     * @param methodName
     * @param parameterTypes
     * @param parameters
     * @return
     * @throws Exception
     */
    private Object invokeCglibMethod(Object serviceBean, Class<?> serviceBeanClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception {
        logger.info("invokeCglibMethod ...........");
        FastClass serviceClass = FastClass.create(serviceBeanClass);
        FastMethod method = serviceClass.getMethod(methodName, parameterTypes);
        return method.invoke(serviceBean,parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常处理
        cause.printStackTrace();
        ctx.close();
    }

}
