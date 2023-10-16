package com.qiangzengy.rpc.test.scanner;

import com.qiangzengy.rpc.annotation.RpcReference;
import com.qiangzengy.rpc.common.scanner.ClassScanner;
import com.qiangzengy.rpc.common.scanner.RpcServiceScanner;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author qiangzeng
 * @date 2023/10/10
 */
public class ScannerTest {


    @Test
    public void testScannerClassNameList() throws Exception {
        List<String> classNameList = ClassScanner.listClassByPackageName("com.qiangzengy.rpc.test.scanner", true);
        classNameList.forEach(System.out::println);
    }

    @Test
    public void testScannerClassNameListByRpcReference() throws Exception {
        Map<String, Object> stringObjectMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService("com.qiangzengy.rpc.test.scanner");
    }

}
