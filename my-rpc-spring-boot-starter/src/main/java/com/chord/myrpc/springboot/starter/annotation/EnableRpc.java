package com.chord.myrpc.springboot.starter.annotation;

import com.chord.myrpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import com.chord.myrpc.springboot.starter.bootstrap.RpcInitBootstrap;
import com.chord.myrpc.springboot.starter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解 - 启用 MyRPC
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
// 仅在用户使用 @EnableRpc 注解时，才启动 RPC 框架，注入启动类的 bean。
// 所以，可以通过给 EnableRpc 增加 @Import 注解，来注册我们自定义的启动类，实现灵活的可选加载。
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要启动 server
     * @return
     */
    boolean needServer() default true;
}
