package com.chord.example;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.chord.example.common.model.User;
import com.chord.example.common.service.UserService;
import com.chord.myrpc.model.RpcRequest;
import com.chord.myrpc.model.RpcResponse;
import com.chord.myrpc.serializer.JdkSerializer;
import com.chord.myrpc.serializer.Serializer;

import java.io.IOException;

/**
 * UserService类的静态代理
 * <p>构造 HTTP 请求去调用服务提供者<p/>
 */
public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {
        // 指定序列化器
        Serializer serializer = new JdkSerializer();

        // 发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(bodyBytes)
                    .execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
