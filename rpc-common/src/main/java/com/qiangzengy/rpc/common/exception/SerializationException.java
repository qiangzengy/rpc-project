package com.qiangzengy.rpc.common.exception;

/**
 * @author qiangzeng
 * @date 2023/10/23
 */
public class SerializationException extends RuntimeException{


    public SerializationException(Throwable cause) {
        super(cause);
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
