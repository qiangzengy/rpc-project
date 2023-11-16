package com.qiangzengy.rpc.consumer.common.context;

import com.qiangzengy.rpc.consumer.common.future.RpcFuture;

/**
 * @author qiangzeng
 * @date 2023/11/16
 */
public class RpcContext {

    private RpcContext(){

    }

    private static final RpcContext AGENT = new RpcContext();

    /**
     * 存放RpcFuture，每个线程维护RpcFuture时，都是相互隔离的，
     * RpcContext类维护的RpcFuture会在RPC框架全局有效
     */
    private static final InheritableThreadLocal<RpcFuture> RPC_FUTURE_INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static RpcContext getContext(){
        return AGENT;
    }

    public RpcFuture getRpcFuture(){
        return RPC_FUTURE_INHERITABLE_THREAD_LOCAL.get();
    }

    public void setRpcFuture(RpcFuture rpcFuture){
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.set(rpcFuture);
    }

    public void removeRpcFuture(){
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.remove();
    }

}
