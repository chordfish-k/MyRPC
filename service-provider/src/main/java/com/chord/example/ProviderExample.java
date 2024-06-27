package com.chord.example;

import com.chord.example.common.service.UserService;
import com.chord.example.service.UserServiceImpl;
import com.chord.myrpc.RpcApplication;
import com.chord.myrpc.config.RegistryConfig;
import com.chord.myrpc.config.RpcConfig;
import com.chord.myrpc.model.ServiceMetaInfo;
import com.chord.myrpc.registry.LocalRegistry;
import com.chord.myrpc.registry.Registry;
import com.chord.myrpc.registry.RegistryFactory;
import com.chord.myrpc.server.HttpServer;
import com.chord.myrpc.server.VertxHttpServer;
import com.chord.myrpc.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 增强版RPC项目的示例提供者
 */
public class ProviderExample {

    public static void main(String[] args) {
        // RPC框架初始化
        RpcApplication.init(Arrays.asList(args));

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        // 注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
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


        // 启动web服务
//        HttpServer httpServer = new VertxHttpServer();
//        httpServer.start(RpcApplication.getRpcConfig().getServerPort());

        // 启动 TCP 服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.start(RpcApplication.getRpcConfig().getServerPort());
    }
}
