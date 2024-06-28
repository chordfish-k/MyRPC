package com.chord.myrpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.chord.myrpc.RpcApplication;
import com.chord.myrpc.config.RpcConfig;
import com.chord.myrpc.constant.RpcConstant;
import com.chord.myrpc.fault.retry.RetryStrategy;
import com.chord.myrpc.loadbalancer.LoadBalancer;
import com.chord.myrpc.model.RpcRequest;
import com.chord.myrpc.model.RpcResponse;
import com.chord.myrpc.model.ServiceMetaInfo;
import com.chord.myrpc.registry.Registry;
import com.chord.myrpc.serializer.Serializer;
import com.chord.myrpc.serializer.SerializerFactory;
import com.chord.myrpc.server.tcp.VertxTcpClient;
import com.chord.myrpc.spi.SpiFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务代理 (JDK 动态代理)
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            // 从注册中心获取服务提供者请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = SpiFactory.getInstance(Registry.class, rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无对应服务地址");
            }

            // 负载均衡
            LoadBalancer loadBalancer = SpiFactory.getInstance(LoadBalancer.class, rpcConfig.getLoadBalancer());
            // 将调用方法名（请求路径）作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("clientIP", RpcApplication.getRpcConfig().getServerHost() + ":" + RpcApplication.getRpcConfig().getServerPort());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            // 发送 TCP 请求，使用重试机制
            RetryStrategy retryStrategy = SpiFactory.getInstance(RetryStrategy.class, rpcConfig.getRetryStrategy());
            RpcResponse rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.request(rpcRequest, selectedServiceMetaInfo)
            );
            return rpcResponse.getData();
        } catch (Exception e) {
            throw e.getCause();
        }
    }
}
