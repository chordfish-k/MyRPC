package com.chord.myrpc.springboot.starter.bootstrap;

import com.chord.myrpc.RpcApplication;
import com.chord.myrpc.config.RpcConfig;
import com.chord.myrpc.server.tcp.VertxTcpServer;
import com.chord.myrpc.springboot.starter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * RPC 框架启动 <br/>
 * 在 Spring 框架初始化时，获取 @EnableRpc 注解的熟悉，并初始化 RPC 框架
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 EnableRpc 注解的属性值
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");

        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();

        // 全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动服务器
        if (needServer) {
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.start(rpcConfig.getServerPort());
        } else {
            log.info("不启动 RPC 服务器");
        }
    }
}
