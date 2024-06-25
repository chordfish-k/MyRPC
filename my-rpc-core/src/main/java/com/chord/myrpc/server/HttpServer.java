package com.chord.myrpc.server;

/**
 * HTTP 服务器接口
 */
public interface HttpServer {

    /**
     * 启动服务器
     * s
     * @param port
     */
    void start(int port);
}
