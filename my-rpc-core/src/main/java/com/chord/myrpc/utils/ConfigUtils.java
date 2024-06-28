package com.chord.myrpc.utils;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
import com.chord.myrpc.config.RpcConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
        StringBuilder configFileBuilder = new StringBuilder("myrpc");
        if (StrUtil.isNotBlank(env)) {
            configFileBuilder.append("-").append(env);
        }

        // 将props里的以prefix开头的项填入对象
        T res = null;

        try {
            Props props = new Props(configFileBuilder + ".properties");
            // 遍历args并覆盖
            if (args != null) {
                for (int i = 0; i < args.size(); i++) {
                    String arg = args.get(i);
                    if (arg.startsWith("--")) {
                        String[] kv = arg.substring(2).split("=");
                        Assert.assertEquals(2, kv.length);
                        props.setProperty(kv[0], kv[1]);
                    }
                }
            }
            res = props.toBean(clazz, prefix);
        } catch (Exception e) {
            res = loadOtherSuffixConfig(clazz, prefix, configFileBuilder.toString(), "yml");
            if (res == null) {
                res = loadOtherSuffixConfig(clazz, prefix, configFileBuilder.toString(), "yaml");
            }
        }

        return res;
    }

    private static  <T> T loadOtherSuffixConfig(Class<T> clazz, String cfgPrefix, String cfgName, String fileSuffix) {
        T res = null;
        Resource resource = null;
        try {
            resource = ResourceUtil.getResourceObj(cfgName + "." + fileSuffix);
        } catch (Exception e) {
            return res;
        }

        Dict dict = YamlUtil.load(resource.getReader(StandardCharsets.UTF_8));
        Object cfg = dict.get(cfgPrefix);
        // 转换成 clazz 类对象
        if (cfg != null) {
            ObjectMapper mapper = new ObjectMapper();
            res = mapper.convertValue(cfg, clazz);
        }
        return res;
    }
}
