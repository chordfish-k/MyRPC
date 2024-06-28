package com.chord.myrpc.springboot.starter.bootstrap;

import com.chord.myrpc.RpcApplication;
import com.chord.myrpc.config.RegistryConfig;
import com.chord.myrpc.config.RpcConfig;
import com.chord.myrpc.model.ServiceMetaInfo;
import com.chord.myrpc.registry.LocalRegistry;
import com.chord.myrpc.registry.Registry;
import com.chord.myrpc.spi.SpiFactory;
import com.chord.myrpc.springboot.starter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * RPC 服务提供者启动
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {

    /**
     * Bean 初始化后执行，注册服务
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService != null) {
            // 1.获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if (interfaceClass == void.class) {
                // 默认选取第一个接口的类型
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 2.注册服务
            // 本地注册
            LocalRegistry.register(serviceName, beanClass);
            // 获取全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = SpiFactory.getInstance(Registry.class, registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }

            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
