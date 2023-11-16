package com.qiangzengy.rpc.consumer.common.future;

import com.qiangzengy.rpc.consumer.common.callback.AsyncRpcCallback;
import com.qiangzengy.rpc.consumer.common.threadpool.ClientThreadPool;
import com.qiangzengy.rpc.protocol.RpcProtocol;
import com.qiangzengy.rpc.protocol.request.RpcRequest;
import com.qiangzengy.rpc.protocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author qiangzeng
 * @date 2023/11/15
 */
public class RpcFuture extends CompletableFuture {

    private Logger logger = LoggerFactory.getLogger(RpcFuture.class);


    private Sync sync;

    private RpcProtocol<RpcRequest> requestRpcProtocol;

    private RpcProtocol<RpcResponse> responseRpcProtocol;

    /**
     * 存放回调接口的
     */
    private List<AsyncRpcCallback> pendingCallbacks = new ArrayList<>();

    /**
     * 添加和执行回调方法时，进行加锁和解锁操作
     */
    private ReentrantLock lock = new ReentrantLock();

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
        // 新增的调用invokeCallbacks方法
        invokeCallbacks();
        long responseTime = System.currentTimeMillis() -startTime;
        if (responseTime > this.responseTimeThresgold){
            logger.warn("");
        }

    }

    private void runCallback(final AsyncRpcCallback callback){
        final RpcResponse res = this.responseRpcProtocol.getBody();
        ClientThreadPool.submit(
                () -> {
                    if (res.isError()){
                        callback.onException(new RuntimeException("Response error",new Throwable(res.getError())));
                    }else {
                        callback.onSuccess(res.getResult());
                    }
                }
        );
    }

    /**
     * 用于外部服务添加回调接口实例对象到pendingCallbacks集合中
     * @param callback
     * @return
     */
    public RpcFuture addCallback(AsyncRpcCallback callback){
        lock.lock();
        try {
            if (isDone()){
                runCallback(callback);
            }else {
                this.pendingCallbacks.add(callback);
            }
        }finally {
            lock.unlock();
        }
        return this;

    }

    /**
     * 用于依次执行pendingCallbacks集合中回调接口的方法
     */
    public void invokeCallbacks(){
        lock.lock();
        try {
            for (AsyncRpcCallback pendingCallback : pendingCallbacks) {
                runCallback(pendingCallback);
            }
        }finally {
            lock.unlock();
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
