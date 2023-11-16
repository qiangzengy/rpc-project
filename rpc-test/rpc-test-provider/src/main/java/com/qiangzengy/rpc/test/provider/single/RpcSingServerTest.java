package com.qiangzengy.rpc.test.provider.single;

import com.qiangzengy.rpc.constants.RpcConstants;
import com.qiangzengy.rpc.provider.RpcSingleServer;
import org.junit.Test;

/**
 * @author qiangzeng
 * @date 2023/10/19
 */
public class RpcSingServerTest {

    @Test
   public void startSingleServer(){

        RpcSingleServer rpcSingleServer = new RpcSingleServer("127.0.0.1:27880","com.qiangzengy.rpc.test", RpcConstants.REFLECT_TYPE_CGLIB);
        rpcSingleServer.startNettyServer();
    }
}
