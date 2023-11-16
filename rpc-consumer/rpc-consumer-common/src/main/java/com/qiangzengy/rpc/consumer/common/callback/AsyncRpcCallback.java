package com.qiangzengy.rpc.consumer.common.callback;

/**
 * @author qiangzeng
 * @date 2023/11/16
 */
public interface AsyncRpcCallback {

    /**
     * 成功后的回调方法
     * @param o
     */
    void onSuccess(Object o);

    /**
     * 失败后的回调方法
     * @param e
     */
    void onException(Exception e);


}
