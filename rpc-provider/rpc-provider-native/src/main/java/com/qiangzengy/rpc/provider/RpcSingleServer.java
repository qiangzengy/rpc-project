package com.qiangzengy.rpc.provider;

import com.qiangzengy.rpc.common.scanner.RpcServiceScanner;
import com.qiangzengy.rpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author qiangzeng
 * @date 2023/10/19
 * 用Java方式启动，不依赖Spring
 */
public class RpcSingleServer extends BaseServer {

    private Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

    public RpcSingleServer(String serverAddress, String scanPackage, String reflectType) {
        super(serverAddress,reflectType);
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(scanPackage);
        } catch (IOException e) {
            logger.error("RPC Server init error",e);
        }

    }
}
