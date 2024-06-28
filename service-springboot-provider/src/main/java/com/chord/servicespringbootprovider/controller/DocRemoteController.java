package com.chord.servicespringbootprovider.controller;

import com.chord.example.common.service.DocService;
import com.chord.myrpc.springboot.starter.annotation.RpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doc")
public class DocRemoteController {

    @RpcReference
    private DocService docService;

    @GetMapping("/test")
    @ResponseBody
    public String getDoc() {
        return docService.getDoc();
    }
}
