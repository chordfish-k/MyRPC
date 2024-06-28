package com.chord.myrpc.server.tcp;

import com.chord.myrpc.model.RpcRequest;
import com.chord.myrpc.model.RpcResponse;
import com.chord.myrpc.protocol.ProtocolMessage;
import com.chord.myrpc.protocol.ProtocolMessageDecoder;
import com.chord.myrpc.protocol.ProtocolMessageEncoder;
import com.chord.myrpc.protocol.ProtocolMessageTypeEnum;
import com.chord.myrpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket netSocket) {
        // 使用装饰器模式增强 buffer handler
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                buffer -> {
                    // 解码
                    ProtocolMessage<RpcRequest> protocolMessage;
                    try {
                        protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息解码错误");
                    }
                    RpcRequest rpcRequest = protocolMessage.getBody();

                    // 处理请求
                    // 构造响应结果对象
                    RpcResponse rpcResponse = new RpcResponse();
                    try {
                        // 获取要调用的服务实现类，通过反射调用
                        Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                        Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                        Object result = method.invoke(implClass.getDeclaredConstructor().newInstance(), rpcRequest.getArgs());
                        // 封装返回结果
                        rpcResponse.setData(result);
                        rpcResponse.setDataType(method.getReturnType());
                        rpcResponse.setMessage("ok");
                    } catch (Exception e) {
                        Throwable cause = e.getCause();
                        cause.printStackTrace();
                        rpcResponse.setMessage(cause.getMessage());
                        rpcResponse.setException(true);
                    }

                    // 编码，发送响应
                    ProtocolMessage.Header header = protocolMessage.getHeader();
                    header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
                    ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>();
                    responseProtocolMessage.setHeader(header);
                    responseProtocolMessage.setBody(rpcResponse);
                    try {
                        Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                        netSocket.write(encode);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息编码错误");
                    }
                });

        netSocket.handler(bufferHandlerWrapper);
    }

}
