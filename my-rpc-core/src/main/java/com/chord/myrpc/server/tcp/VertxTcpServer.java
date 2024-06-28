package com.chord.myrpc.server.tcp;

import com.chord.myrpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于 Vert.x 的Tcp服务器
 */
@Slf4j
public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData) {
        // 示例返回
        return "Hello, client!".getBytes();
    }

    @Override
    public void start(int port) {
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();

        // 创建HTTP服务器
        NetServer server = vertx.createNetServer();

        // 处理请求
        server.connectHandler(new TcpServerHandler());

        // 启动HTTP服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("RPC 服务器正在监听 " + port + " 端口");
            } else {
                log.error("RPC 服务器启动失败: " + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().start(8888);
    }
}
