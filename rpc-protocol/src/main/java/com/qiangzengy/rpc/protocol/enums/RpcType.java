package com.qiangzengy.rpc.protocol.enums;

/**
 * @author qiangzeng
 * @date 2023/10/19
 */
public enum RpcType {

    REQUEST(1),

    RESPONSE(2),

    // 心跳数据
    HEARTBEAT(3),

    ;

    private final int type;

    RpcType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public static RpcType findByType(int type) {
        for (RpcType rpcType : RpcType.values()) {
            if (rpcType.getType() == type) {
                return rpcType;
            }
        }
        return null;
    }
}
