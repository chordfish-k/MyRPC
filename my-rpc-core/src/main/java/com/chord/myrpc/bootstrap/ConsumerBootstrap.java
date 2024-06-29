package com.chord.myrpc.bootstrap;

import com.chord.myrpc.RpcApplication;

import java.util.Arrays;
import java.util.List;

/**
 * 服务消费者初始化
 */
public class ConsumerBootstrap {

    /**
     * 初始化
     */
    public static void init() {
        RpcApplication.init();
    }


    /**
     * 初始化
     * @param args
     */
    public static void init(String[] args) {
        RpcApplication.init(Arrays.asList(args));
    }
}
