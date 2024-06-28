package com.chord.servicespringbootprovider.service;

import com.chord.example.common.model.User;
import com.chord.example.common.service.DocService;
import com.chord.example.common.service.UserService;
import com.chord.myrpc.springboot.starter.annotation.RpcReference;
import com.chord.myrpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@RpcService
@Service
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }

    @Override
    public Integer getError() {
        throw new RuntimeException("测试一个错误");
    }
}
