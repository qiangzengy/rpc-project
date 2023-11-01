package com.qiangzengy.rpc.codec;

import com.qiangzengy.rpc.common.utils.SerializationUtils;
import com.qiangzengy.rpc.protocol.RpcProtocol;
import com.qiangzengy.rpc.protocol.header.RpcHeader;
import com.qiangzengy.rpc.serialization.api.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author qiangzeng
 * @date 2023/10/23
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> implements RpcCodec {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        RpcHeader header = msg.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        String serializationType = header.getSerializationType();
        // TODO Serialization是扩展点
        Serialization serialization = getJdkSerialization();
        byteBuf.writeBytes(SerializationUtils.paddingString(serializationType).getBytes("UTF-8"));
        byte[] data = serialization.serialize(msg.getBody());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
//        //异步调用流控分析后置处理器
//        header.setMsgLen(data.length);
//        this.postFlowProcessor(postProcessor, header);
    }
}
