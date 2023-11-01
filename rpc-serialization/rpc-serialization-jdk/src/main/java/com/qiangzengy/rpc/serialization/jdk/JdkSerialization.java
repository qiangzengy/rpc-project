package com.qiangzengy.rpc.serialization.jdk;

import com.qiangzengy.rpc.common.exception.SerializationException;
import com.qiangzengy.rpc.serialization.api.Serialization;
import org.apache.tools.ant.taskdefs.Echo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author qiangzeng
 * @date 2023/10/23
 */
public class JdkSerialization implements Serialization {

    private final Logger logger = LoggerFactory.getLogger(JdkSerialization.class);

    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null){
            throw new SerializationException("serialize obj is null");
        }
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            logger.error("使用jdk序列化异常:",e);
            throw new SerializationException(e.getMessage(),e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        if (data == null){
            throw new SerializationException("serialize date is null");
        }
        try{
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (T) objectInputStream.readObject();
        }catch (Exception e){
            logger.error("使用jdk反序列化异常:",e);
            throw new SerializationException(e.getMessage(),e);
        }
    }
}
