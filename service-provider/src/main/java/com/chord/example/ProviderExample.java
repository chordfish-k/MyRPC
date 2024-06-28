package com.chord.example;

import com.chord.example.common.service.UserService;
import com.chord.example.service.UserServiceImpl;
import com.chord.myrpc.RpcApplication;
import com.chord.myrpc.bootstrap.ProviderBootstrap;
import com.chord.myrpc.config.RegistryConfig;
import com.chord.myrpc.config.RpcConfig;
import com.chord.myrpc.model.ServiceMetaInfo;
import com.chord.myrpc.model.ServiceRegisterInfo;
import com.chord.myrpc.registry.LocalRegistry;
import com.chord.myrpc.registry.Registry;
import com.chord.myrpc.registry.RegistryFactory;
import com.chord.myrpc.server.HttpServer;
import com.chord.myrpc.server.VertxHttpServer;
import com.chord.myrpc.server.tcp.VertxTcpServer;
import com.chord.myrpc.spi.SpiFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 增强版RPC项目的示例提供者
 */
public class ProviderExample {

    public static void main(String[] args) {
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<?> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList, args);
    }
}
