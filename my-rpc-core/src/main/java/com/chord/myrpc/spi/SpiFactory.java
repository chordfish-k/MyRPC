package com.chord.myrpc.spi;

import com.chord.myrpc.loadbalancer.LoadBalancer;
import com.chord.myrpc.loadbalancer.RoundRobinLoadBalancer;

public class SpiFactory {

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static <T> T getInstance(Class<T> spiClass, String key) {
        SpiLoader.load(spiClass);
        return SpiLoader.getInstance(spiClass, key);
    }
}

