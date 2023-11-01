package com.qiangzengy.rpc.codec;

import com.qiangzengy.rpc.common.utils.SerializationUtils;
import com.qiangzengy.rpc.constants.RpcConstants;
import com.qiangzengy.rpc.protocol.RpcProtocol;
import com.qiangzengy.rpc.protocol.enums.RpcType;
import com.qiangzengy.rpc.protocol.header.RpcHeader;
import com.qiangzengy.rpc.protocol.request.RpcRequest;
import com.qiangzengy.rpc.protocol.response.RpcResponse;
import com.qiangzengy.rpc.serialization.api.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @author qiangzeng
 * @date 2023/10/24
 */
public class RpcDecoder extends ByteToMessageDecoder implements RpcCodec {


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() < RpcConstants.HEADER_TOTAL_LEN){
            return;
        }
        byteBuf.markReaderIndex();

        short magic = byteBuf.readShort();
        if (magic != RpcConstants.MAGIC){
            throw new IllegalArgumentException("magic number is illegal," + magic);
        }
        byte msgType = byteBuf.readByte();
        byte status = byteBuf.readByte();
        long requestId = byteBuf.readLong();

        ByteBuf serializationByteBuf = byteBuf.readBytes(SerializationUtils.MIN_SERIALIZATION_TYPE_COUNT);
        String serializationType = SerializationUtils.subString(serializationByteBuf.toString(CharsetUtil.UTF_8));

        int dataLen = byteBuf.readInt();
        if(byteBuf.readableBytes() < dataLen){
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLen];
        byteBuf.readBytes(data);

        RpcType msgTypeEnum = RpcType.findByType(msgType);
        if (msgTypeEnum == null){
            return;
        }

        RpcHeader header = new RpcHeader();
        header.setMagic(magic);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setSerializationType(serializationType);
        header.setMsgLen(dataLen);
        //TODO Serialization是扩展点
        Serialization jdkSerialization = getJdkSerialization();
        switch (msgTypeEnum){
            case REQUEST:
                RpcRequest rpcRequest = jdkSerialization.deserialize(data,RpcRequest.class);
                if (rpcRequest == null){
                    break;
                }
                RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
                requestRpcProtocol.setHeader(header);
                requestRpcProtocol.setBody(rpcRequest);
                out.add(requestRpcProtocol);
                break;
            case RESPONSE:
                RpcResponse response = jdkSerialization.deserialize(data,RpcResponse.class);
                if (response == null){
                    break;
                }
                RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
                responseRpcProtocol.setHeader(header);
                responseRpcProtocol.setBody(response);
                out.add(responseRpcProtocol);
                break;
            case HEARTBEAT:
                break;
        }


    }
}
