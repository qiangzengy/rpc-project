package com.qiangzengy.rpc.consumer.common.future;

import com.qiangzengy.rpc.protocol.RpcProtocol;
import com.qiangzengy.rpc.protocol.request.RpcRequest;
import com.qiangzengy.rpc.protocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;


/**
 * @author qiangzeng
 * @date 2023/11/15
 */
public class RpcFuture extends CompletableFuture {

    private Logger logger = LoggerFactory.getLogger(RpcFuture.class);


    private Sync sync;

    private RpcProtocol<RpcRequest> requestRpcProtocol;

    private RpcProtocol<RpcResponse> responseRpcProtocol;

    private long startTime;

    private long responseTimeThresgold = 5000;

    public RpcFuture(RpcProtocol<RpcRequest> requestRpcProtocol){
        this.sync = new Sync();
        this.requestRpcProtocol = requestRpcProtocol;
        this.startTime = System.currentTimeMillis();

    }


    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(1);
        if (this.responseRpcProtocol != null){
            return this.responseRpcProtocol.getBody().getResult();
        }
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success){
            if (this.responseRpcProtocol != null){
                return this.responseRpcProtocol.getBody().getResult();
            }
            return null;
        }
        throw new RuntimeException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public void done(RpcProtocol<RpcResponse> responseRpcProtocol){
        this.responseRpcProtocol = responseRpcProtocol;
        sync.release(1);
        long responseTime = System.currentTimeMillis() -startTime;
        if (responseTime > this.responseTimeThresgold){
            logger.warn("");
        }

    }

    static class Sync extends AbstractQueuedSynchronizer{

        private final int done = 1;

        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending){
                if (compareAndSetState(pending,done)){
                    return true;
                }
            }
            return false;
        }

        public boolean isDone(){
            int state = getState();
            return state == done;
        }

    }


}
