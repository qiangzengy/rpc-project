package com.qiangzengy.rpc.codec;

import com.qiangzengy.rpc.serialization.api.Serialization;
import com.qiangzengy.rpc.serialization.jdk.JdkSerialization;

/**
 * @author qiangzeng
 * @date 2023/10/23
 */
public interface RpcCodec {

    default Serialization getJdkSerialization(){
        return new JdkSerialization();
    }
}
