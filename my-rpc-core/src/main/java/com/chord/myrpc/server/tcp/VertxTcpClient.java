package com.chord.myrpc.server.tcp;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import com.chord.myrpc.RpcApplication;
import com.chord.myrpc.model.RpcRequest;
import com.chord.myrpc.model.RpcResponse;
import com.chord.myrpc.model.ServiceMetaInfo;
import com.chord.myrpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 基于 Vert.x 的Tcp客户端
 */
@Slf4j
public class VertxTcpClient {

    /**
     * 发送请求
     *
     * @param rpcRequest
     * @param serviceMetaInfo
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static RpcResponse request(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 发送 TCP 请求
        // 创建 Vert.x 实例
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        throw new RuntimeException("连接 TCP 服务器失败");
                    }
                    log.debug("发起 RPC: {}.{} -> host={}, post={}",
                            serviceMetaInfo.getServiceName().substring(serviceMetaInfo.getServiceName().lastIndexOf(".")+1),
                            rpcRequest.getMethodName(), serviceMetaInfo.getServiceHost(), serviceMetaInfo.getServicePort());
                    NetSocket socket = result.result();
                    // 发送数据
                    // 构造消息
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    String serializerKey = RpcApplication.getRpcConfig().getSerializer();
                    ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByValue(serializerKey);
                    if (serializerEnum == null) {
                        throw new RuntimeException("指定的序列化器不存在：" + serializerKey);
                    }
                    header.setSerializer((byte) serializerEnum.getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    // 编码请求
                    try {
                        Buffer encodeBufffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBufffer);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息编码错误");
                    }

                    // 接收响应
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
                                            (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    // 完成异步
                                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("协议消息解码错误", e);
                                }
                            }
                    );
                    socket.handler(bufferHandlerWrapper);
                });

        // 阻塞，直到调用 responseFuture.complete
        // TODO 超时
        RpcResponse rpcResponse = responseFuture.get();

        netClient.close();

        if (BooleanUtil.isTrue(rpcResponse.getException())) {
            throw new RuntimeException("from " +serviceMetaInfo.getServiceHost() +":"+ serviceMetaInfo.getServicePort() + ": " + rpcResponse.getMessage());

        }
        return rpcResponse;
    }
}
