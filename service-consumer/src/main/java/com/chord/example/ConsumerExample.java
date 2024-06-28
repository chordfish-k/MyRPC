package com.chord.example;

import com.chord.example.common.model.User;
import com.chord.example.common.service.UserService;
import com.chord.myrpc.config.RpcConfig;
import com.chord.myrpc.proxy.ServiceProxyFactory;
import com.chord.myrpc.utils.ConfigUtils;

/**
 * 增强版RPC项目的示例消费者类
 */
public class ConsumerExample {

    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("Chord");
        // 测试rpc调用
        User newUser = userService.getUser(user);
        System.out.println(newUser == null ? "user == null" : newUser.getName());

        userService.getError();
    }
}
