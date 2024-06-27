package com.chord.myrpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

/**
 * 配置项工具类<br/>
 * 读取配置文件并返回配置对象，可以简化调用
 */
public class ConfigUtils {

    /**
     * 加载配置对象
     * @param clazz
     * @param prefix
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix) {
        return loadConfig(clazz, prefix, null, "");
    }

    /**
     * 加载配置对象 支持使用args
     * @param clazz
     * @param args
     * @param prefix
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, List<String> args) {
        return loadConfig(clazz, prefix, args, "");
    }

    /**
     * 加载配置对象，支持区分环境
     * @param clazz
     * @param prefix
     * @param env
     * @return
     * @param <T>
     */
    private static <T> T loadConfig(Class<T> clazz, String prefix, List<String> args, String env) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(env)) {
            configFileBuilder.append("-").append(env);
        }
        configFileBuilder.append(".properties");
        Props props = new Props(configFileBuilder.toString());
        // 遍历args并覆盖
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            if (arg.startsWith("--")) {
                String[] kv = arg.substring(2).split("=");
                Assert.assertEquals(2, kv.length);
                props.setProperty(kv[0], kv[1]);
            }
        }
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        // 将props里的以prefix开头的项填入对象
        T res = props.toBean(clazz, prefix);
        return res;
    }
}
