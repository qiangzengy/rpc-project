package com.qiangzengy.rpc.protocol.enums;

/**
 * @author qiangzeng
 * @date 2023/11/6
 * 服务器调用状态
 */
public enum RpcStatus {

    SUCCESS(0),
    FAIL(1)
    ;

    private int code;

    RpcStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
