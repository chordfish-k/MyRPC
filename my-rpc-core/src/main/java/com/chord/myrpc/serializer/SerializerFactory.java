package com.chord.myrpc.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器工厂
 */
public class SerializerFactory {

    /**
     * 序列化器映射
     */
    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>() {{
        put(SerializerKeys.JDK, new JdkSerializer());
        put(SerializerKeys.JSON, new JsonSerializer());
        put(SerializerKeys.KRYO, new KryoSerializer());
        put(SerializerKeys.HESSIAN, new HessianSerializer());
    }};

    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);

    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        System.out.println(key);
        return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
    }
}
