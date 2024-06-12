package com.chord.example;

import com.chord.myrpc.server.HttpServer;
import com.chord.myrpc.server.VertxHttpServer;

/**
 * 服务提供者启动类
 */
public class ServiceProviderExample {

    public static void main(String[] args) {
        // 启动web服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.start(8080);
    }
}
