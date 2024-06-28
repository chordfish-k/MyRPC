package com.chord.servicespringbootconsumer.service;

import cn.hutool.core.lang.UUID;
import com.chord.example.common.model.User;
import com.chord.example.common.service.DocService;
import com.chord.example.common.service.UserService;
import com.chord.myrpc.springboot.starter.annotation.RpcReference;
import com.chord.myrpc.springboot.starter.annotation.RpcService;
import org.springframework.stereotype.Service;

@RpcService
public class DocServiceImpl implements DocService {

    @Override
    public String getDoc() {
        return UUID.fastUUID().toString();
    }
}
