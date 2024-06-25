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
        if (userService != null) {
            User newUser = userService.getUser(user);
            if (newUser != null) {
                System.out.println(newUser.getName());
            } else {
                System.out.println("user == null");
            }
        }
        long number = userService.getNumber();
        System.out.println(number);
    }
}
