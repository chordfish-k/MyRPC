package com.chord.myrpc.config;

import com.chord.myrpc.registry.RegisterKeys;
import lombok.Data;

/**
 * RPC 框架注册中心配置
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心类别
     */
    private String registry = RegisterKeys.ETCD;

    /**
     * 注册中心地址
     */
    private String address;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间（10000ms=10s）
     */
    private Long timeout = 10000L;


    public String getAddress() {
        if (address == null || address.isEmpty()) {
            address = "http://localhost:" + getDefaultPortByRegisterType();
            return address;
        }
        if (address.startsWith("http")) {
            return address;
        }
        return "http://" + address;
    }

    private String getDefaultPortByRegisterType() {
        if (registry.equals(RegisterKeys.ETCD)) {
            return "2380";
        }
        if (registry.equals(RegisterKeys.ZOOKEEPER)) {
            return "2181";
        }
        if (registry.equals(RegisterKeys.REDIS)) {
            return "6379";
        }
        return "8080";
    }
}
