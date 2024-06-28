package com.chord.servicespringbootconsumer.controller;

import com.chord.example.common.model.User;
import com.chord.example.common.service.UserService;
import com.chord.myrpc.springboot.starter.annotation.RpcReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/example")
public class UserController {

    @RpcReference
    private UserService userService;

    @GetMapping("/test/{name}")
    @ResponseBody
    public User test(@PathVariable String name) {
        User user = new User();
        user.setName(name);
        User user1 = userService.getUser(user);
        return user1;
    }
}
