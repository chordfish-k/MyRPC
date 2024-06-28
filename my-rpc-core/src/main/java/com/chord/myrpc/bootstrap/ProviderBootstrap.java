package com.chord.myrpc.bootstrap;

import com.chord.myrpc.RpcApplication;
import com.chord.myrpc.config.RegistryConfig;
import com.chord.myrpc.config.RpcConfig;
import com.chord.myrpc.model.ServiceMetaInfo;
import com.chord.myrpc.model.ServiceRegisterInfo;
import com.chord.myrpc.registry.LocalRegistry;
import com.chord.myrpc.registry.Registry;
import com.chord.myrpc.server.tcp.VertxTcpServer;
import com.chord.myrpc.spi.SpiFactory;

import java.util.List;

/**
 * 服务提供者初始化
 */
public class ProviderBootstrap {

    /**
     * 初始化
     * @param serviceRegisterInfoList
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        init(serviceRegisterInfoList, null);
    }

    /**
     * 初始化
     * @param serviceRegisterInfoList
     * @param args
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList, String[] args) {
        // RPC 框架初始化 （配置和注册中心）
        RpcApplication.init();
        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = SpiFactory.getInstance(Registry.class, registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            serviceMetaInfo.setRegistryTimeNow();
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // 启动 TCP 服务
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.start(RpcApplication.getRpcConfig().getServerPort());
        }
    }
}
