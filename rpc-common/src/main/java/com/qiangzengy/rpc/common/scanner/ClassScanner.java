package com.qiangzengy.rpc.common.scanner;


import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author qiangzengy@gmail.com
 * @date 2023/5/4
 */
public class ClassScanner {

    /**
     * 文件，扫描当前工程中指定包下的所有类信息
     */
    private static final String PROTOCOL_FILE = "file";

    /**
     * jar包，扫描jar文件中指定包下面的所有类信息
     */
    private static final String PROTOCOL_JAR = "jar";

    /**
     * class文件后缀，扫描的过程中指定需要处理的文件的后缀信息
     */
    private static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * 扫描指定包下的所有类信息
     * @param packageName 指定的包名
     * @param recursive 是否递归扫描，true:是  false:否
     * @return 指定包下所有的完整类名的List集合
     * @throws Exception
     */
    public static List<String> listClassByPackageName(String packageName, boolean recursive) throws IOException {

        List<String> classNames = new ArrayList<>();
        String packageDirName = packageName.replace('.', '/');
        //定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        while (resources.hasMoreElements()){
            URL url = resources.nextElement();
            // 获取协议名称
            String protocol = url.getProtocol();
            if (PROTOCOL_FILE.equals(protocol)){
                //获取包的物理路径
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                //以文件的方式扫描整个包下的文件 并添加到集合中
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, classNames);
            }else if(PROTOCOL_JAR.equals(protocol)){
                packageName = findAndAddClassesInPackageByJar(packageName, packageDirName, recursive, classNames, url);
            }
        }
        return classNames;
    }

    /**
     * 扫描当前包下的所有类信息
     *
     * @param packageName   包名
     * @param packagePath   包的路径
     * @param recursive     是否递归调用
     * @param classNameList 类集合的名称
     */
    private static void findAndAddClassesInPackageByFile(String packageName,
                                                         String packagePath,
                                                         final boolean recursive,
                                                         List<String> classNameList) {

        // 获取此包的目录 建立一个文件
        File file = new File(packagePath);
        // 如果不存在或者不是一个目录，return
        if (!file.exists() || !file.isDirectory()) {
            return;
        }
        // 获取包下的所有文件(包括目录)
        File[] files = file.listFiles(
                // 自定义过滤规则 如果可以循环（包含子目录）或以.class结尾的文件
                (pathname) -> (recursive && pathname.isDirectory()) || (file.getName().endsWith(".class"))
        );
        // 循环所有的文件
        for (File newFile : files) {
            if (newFile.isDirectory()) {
                // 递归思想
                findAndAddClassesInPackageByFile(
                        packageName + "." + newFile.getName(),
                        newFile.getAbsolutePath(),
                        recursive,
                        classNameList);
            } else {
                // 去掉.class,获取类名
                String className = newFile.getName().substring(0, newFile.getName().length() - 6);
                classNameList.add(className);
            }
        }
    }

    /**
     * 扫描Jar文件下类的信息
     *
     * @param packageName
     * @param packageDirName
     * @param recursive
     * @param classNameList
     * @param url
     * @return
     */
    private static String findAndAddClassesInPackageByJar(
            String packageName,
            String packageDirName,
            final boolean recursive,
            List<String> classNameList,
            URL url) throws IOException {
        // 定义一个JarFile
        JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
        // 从此jar包得到一个枚举类
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            // 获取jar里的一个实体可以是目录 和一些jar包里的其它文件，如META-INF等文件
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            // 如果前半部分和定义的包名相同
            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                String currentPackageDir = "";
                // 如果以/结尾 是一个包
                if (idx != -1) {
                    currentPackageDir = name.substring(0, idx);
                    packageName = currentPackageDir.replace('/', '.');
                }
                // 如果可以迭代下去 并且是一个包
                if ((idx != -1 && currentPackageDir.equals(packageDirName)) || recursive) {
                    if (name.endsWith(CLASS_FILE_SUFFIX) && !jarEntry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - CLASS_FILE_SUFFIX.length());
                        classNameList.add(packageName + '.' + className);
                    }
                }
            }
        }
        return packageName;
    }
}
