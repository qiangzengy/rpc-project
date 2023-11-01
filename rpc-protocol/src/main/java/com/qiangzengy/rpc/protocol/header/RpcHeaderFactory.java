package com.qiangzengy.rpc.protocol.header;

import com.qiangzengy.rpc.common.id.IdFactory;
import com.qiangzengy.rpc.constants.RpcConstants;

/**
 * @author qiangzeng
 * @date 2023/10/20
 */
public class RpcHeaderFactory {


    /**
     * 创建一个请求类型的Header对象
     * @param serializationType
     * @param messageType
     * @return
     */
    public static RpcHeader getRequestHeader(String serializationType, int messageType){
        RpcHeader header = new RpcHeader();
        long requestId = IdFactory.getId();
        header.setMagic(RpcConstants.MAGIC);
        header.setRequestId(requestId);
        header.setMsgType((byte) messageType);
        header.setStatus((byte) 0x1);
        header.setSerializationType(serializationType);
        return header;
    }
}
