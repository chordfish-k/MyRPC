package com.chord.example;

import com.chord.example.common.model.User;
import com.chord.example.common.service.UserService;
import com.chord.myrpc.proxy.ServiceProxyFactory;

/**
 * 服务消费者启动类
 */
public class ServiceConsumerExample {

    public static void main(String[] args) {
        // TODO 需要获取 UserService 的实现类对象
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
    }
}
