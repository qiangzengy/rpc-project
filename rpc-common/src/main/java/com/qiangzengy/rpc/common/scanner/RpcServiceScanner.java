package com.qiangzengy.rpc.common.scanner;

import com.qiangzengy.rpc.annotation.RpcService;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiangzengy@gmail.com
 * @date 2023/5/8
 */
public class RpcServiceScanner extends ClassScanner{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceScanner.class);

    /**
     * 扫描指定包下的类，并筛选使用@RpcService注解标注的类
     * @param scanPackage
     * @return
     */
    public static Map<String,Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService(String scanPackage) throws IOException {
        Map<String,Object> map = new HashMap<>();
        List<String> list = listClassByPackageName(scanPackage, true);
        if (list == null || list.size() == 0){
            return map;
        }
        list.forEach(
                className -> {
                    try {
                        Class<?> clazz = Class.forName(className);
                        LOGGER.debug("Class =======> :{}",clazz.getName());
                        RpcService rpcService = clazz.getAnnotation(RpcService.class);
                        if (rpcService != null){
                            LOGGER.debug("rpcService=========>:{}",rpcService.getClass().getName());
                        }
                    } catch (Exception e) {
                        LOGGER.error("scan classes throws exception: {}", e);
                    }
                }
        );
        return null;
    }

}
