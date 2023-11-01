package com.qiangzengy.rpc.serialization.api;

import com.qiangzengy.rpc.constants.RpcConstants;

/**
 * @author qiangzeng
 * @date 2023/10/23
 */
//@SPI(RpcConstants.SERIALIZATION_JDK)
public interface Serialization {


    /**
     * 序列化
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     */
    <T> T deserialize(byte[] data, Class<T> cls);
}
