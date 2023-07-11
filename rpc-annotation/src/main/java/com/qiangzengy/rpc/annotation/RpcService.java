package com.qiangzengy.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Target 描述了注解修饰的对象范围
 * ElementType.TYPE: 类、接口、注解、enum
 * ElementType.CONSTRUCTOR: 构造函数
 * ElementType.FIELD: 成员变量、对象、属性、枚举的常量
 * ElementType.LOCAL_VARIABLE: 局部变量
 * ElementType.METHOD: 方法
 * ElementType.PACKAGE: 包
 * ElementType.PARAMETER: 参数
 * ElementType.ANNOTATION_TYPE): 注解
 * ElementType.TYPE_PARAMETER：类型参数，表示这个注解可以用在 Type的声明式前,jdk1.8引入。
 * ElementType.TYPE_USE：类型的注解，表示这个注解可以用在所有使用Type的地方（如：泛型，类型转换等），jdk1.8引入。
 */

/**
 * @author qiangzengy@gmail.com
 * @date 2023/4/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    /**
     * 接口的Class
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 接口名
     * @return
     */
    String interClassName() default "";

    /**
     * 版本号
     * @return
     */
    String version() default "1.0.0";

    /**
     * 服务分组，默认空
     * @return
     */
    String group() default "";
}
