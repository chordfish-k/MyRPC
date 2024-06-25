package com.chord.myrpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

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
        return loadConfig(clazz, prefix, "");
    }

    /**
     * 加载配置对象，支持区分环境
     * @param clazz
     * @param prefix
     * @param env
     * @return
     * @param <T>
     */
    private static <T> T loadConfig(Class<T> clazz, String prefix, String env) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(env)) {
            configFileBuilder.append("-").append(env);
        }
        configFileBuilder.append(".properties");
        Props props = new Props(configFileBuilder.toString());
        // 将props里的以prefix开头的项填入对象
        return props.toBean(clazz, prefix);
    }
}
