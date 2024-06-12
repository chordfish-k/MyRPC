package com.chord.example;

import com.chord.example.common.service.UserService;
import com.chord.example.service.UserServiceImpl;
import com.chord.myrpc.registry.LocalRegistry;
import com.chord.myrpc.server.HttpServer;
import com.chord.myrpc.server.VertxHttpServer;

/**
 * 服务提供者启动类
 */
public class ServiceProviderExample {

    public static void main(String[] args) {
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.start(8080);
    }
}
