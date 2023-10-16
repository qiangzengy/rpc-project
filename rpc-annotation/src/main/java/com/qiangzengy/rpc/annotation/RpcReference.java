package com.qiangzengy.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务消费者注解
 * @author qiangzengy@gmail.com
 * @date 2023/5/4
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

    /**
     * 版本号
     * @return
     */
    String version() default "1.0.0";

    /**
     * 注册中心类型，zk,nacos,etcd,consul
     */
    String registryType() default "zookeeper";

    /**
     * 注册地址
     */
    String registryAddress() default "127.0.0.1:2181";

    /**
     * 负载均衡类型，默认zk的一致性hash
     */
    String loadBalanceType() default "zkconsistenthash";

    /**
     * 序列化类型，protostuff，kryo，json, jdk, hessian2,fst
     */
    String serializationType() default "protostuff";

    /**
     * 超时时间
     */
    long timeout() default 5000;

    /**
     * 是否异步执行
     */
    boolean async() default false;

    /**
     * 是否单向调用
     */
    boolean oneway() default false;

    /**
     * 代理类型，jdk, javassist,cglib
     */
    String proxy() default "jdk";

    /**
     * 服务分组
     */
    String group() default "";
}
