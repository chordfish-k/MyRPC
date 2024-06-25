package com.chord.myrpc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂，用于创建服务代理对象
 */
public class ServiceProxyFactory {

    public static <T> T getProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy());
    }
}
