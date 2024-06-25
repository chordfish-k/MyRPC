package com.chord.myrpc;

import com.chord.myrpc.config.RpcConfig;
import com.chord.myrpc.constant.RpcConstant;
import com.chord.myrpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架应用<br>
 * 存放了项目全局用到的遍历。双检锁单例模式
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig config) {
        rpcConfig = config;
        log.info("RPC init, config = {}", config.toString());
    }

    /**
     * 初始化
     */
    public static void init() {
        RpcConfig config;
        try {
            config = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 加载配置失败，使用默认值
            config = new RpcConfig();
        }
        init(config);
    }

    /**
     * 获取配置单例<br>
     * 考虑到多线程，要使用锁，并且检查两次null
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
