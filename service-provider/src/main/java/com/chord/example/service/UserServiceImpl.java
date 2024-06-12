package com.chord.example.service;

import com.chord.example.common.model.User;
import com.chord.example.common.service.UserService;

public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
