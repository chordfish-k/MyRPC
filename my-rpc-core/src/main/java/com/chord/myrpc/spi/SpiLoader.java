package com.chord.myrpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.chord.myrpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支持键值对映射的 SPI 加载器
 */
@Slf4j
public class SpiLoader {

    /**
     * 存储已加载的类：接口名 => (key => 实现类)
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存（单例模式）：类路径 => 对象实例
     */
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = List.of(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("加载所有 SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 获取某个接口的实例，懒加载，单例
     * @param tClass
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", tClassName, key));
        }
        // 获取到要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        // 从实例缓存中加载指定类型的实例
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.getDeclaredConstructor().newInstance());
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(String.format("%s 类实例化失败", implClassName), e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }

    /**
     * 加载某个类型的所有实现类
     * @param loadClass
     * @return
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        // 如果已经加载，则直接返回
        if(loaderMap.containsKey(loadClass.getName())) {
            return loaderMap.get(loadClass.getName());
        }
        if (loadClass.getName().equals("com.chord.myrpc.fault.retry.RetryStrategy")){
            System.out.println(loadClass.getName());
        }
        log.info("加载类型为 {} 的 SPI", loadClass.getName());
        // 扫描路径，用户自定义的SPI优先级(覆盖顺序)高于系统SPI
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            // 读取每个资源文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0].trim();
                            String className = strArray[1].trim();
                            try {
                                Class<?> clazz = Class.forName(className);
                                keyClassMap.put(key, clazz);
                                log.info("添加 " + className);
                            } catch (NoClassDefFoundError e) {
                                log.warn("未添加 " + className + "，将无法使用");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (Exception e) {
                    log.error("SPI 资源加载错误", e);
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }
}
