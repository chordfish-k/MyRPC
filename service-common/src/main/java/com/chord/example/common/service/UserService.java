package com.chord.example.common.service;

import com.chord.example.common.model.User;

public interface UserService {

    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);

    /**
     * 测试Mock用方法，获取数字
     * @return
     */
    default short getNumber() {
        return 1;
    }
}
