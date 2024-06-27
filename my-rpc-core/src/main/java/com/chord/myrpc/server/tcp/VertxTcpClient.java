package com.chord.myrpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于 Vert.x 的Tcp客户端
 */
@Slf4j
public class VertxTcpClient {

    public void start() {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(8888, "localhost", result -> {
            if (result.succeeded()) {
                System.out.println("连接到TCP服务器");
                NetSocket socket = result.result();
                // 发送数据
                socket.write("Hello, server!");
                // 接收响应
                socket.handler(buffer -> {
                    System.out.println("收到服务器的响应：" + buffer.toString());
                });
            }
            else {
                System.out.println("连接TCP服务器失败");
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }
}
