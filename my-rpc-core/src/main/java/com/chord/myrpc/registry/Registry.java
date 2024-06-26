package com.chord.myrpc.registry;

import com.chord.myrpc.config.RegistryConfig;
import com.chord.myrpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心接口
 */
public interface Registry {

    /**
     * 初始化
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 服务端-注册服务
     * @param serviceMetaInfo
     * @throws Exception
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 服务端-注销服务
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 消费端-服务发现，获取某服务的所有节点
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();

    /**
     * 服务端-心跳检测
     */
    void startHearBeat();

    /**
     * 消费端-监听
     * @param serviceNodeKey
     */
    void watch(String serviceNodeKey);
}
