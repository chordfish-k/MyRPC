package com.chord.example;

import com.chord.myrpc.config.RpcConfig;
import com.chord.myrpc.utils.ConfigUtils;

/**
 * 增强版RPC项目的示例消费者类
 */
public class ConsumerExample {

    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}
