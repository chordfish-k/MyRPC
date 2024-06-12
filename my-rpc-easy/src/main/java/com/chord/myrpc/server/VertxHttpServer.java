package com.chord.myrpc.server;

import io.vertx.core.Vertx;

/**
 * Vertx HTTP 服务器
 */
public class VertxHttpServer implements HttpServer{

    /**
     * 启动服务器
     * @param port
     */
    public void start(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建HTTP服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        // 监听端口并处理请求
        server.requestHandler(new VertxHttpServerHandler());

        // 启动HTTP服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Server is now listening on port " + port);
            } else {
                System.out.println("Fail to start server: " + result.cause());
            }
        });
    }
}
