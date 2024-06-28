package com.chord.myrpc.fault.tolerant;

import com.chord.myrpc.RpcApplication;
import com.chord.myrpc.fault.retry.RetryStrategy;
import com.chord.myrpc.loadbalancer.LoadBalancer;
import com.chord.myrpc.model.RpcRequest;
import com.chord.myrpc.model.RpcResponse;
import com.chord.myrpc.model.ServiceMetaInfo;
import com.chord.myrpc.server.tcp.VertxTcpClient;
import com.chord.myrpc.spi.SpiFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 容错策略 - 转移到其它服务节点
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 获取其它服务节点并调用
        RpcRequest rpcRequest = (RpcRequest) context.getOrDefault("rpcRequest", null);
        List<ServiceMetaInfo> serviceMetaInfoList = (List<ServiceMetaInfo>) context.getOrDefault("serviceMetaInfoList", null);
        ServiceMetaInfo selectedServiceMetaInfo = (ServiceMetaInfo) context.getOrDefault("selectedServiceMetaInfo", null);

        if (serviceMetaInfoList == null || selectedServiceMetaInfo == null) {
            return RpcResponse.failed();
        }

        int index = serviceMetaInfoList.indexOf(selectedServiceMetaInfo);
        int size = serviceMetaInfoList.size();
        Random random = new Random(System.currentTimeMillis());
        index = (index + random.nextInt(size-1)+1) % size;
        ServiceMetaInfo serviceMetaInfo = serviceMetaInfoList.get(index);

        RpcResponse rpcResponse = RpcResponse.failed();
        RetryStrategy retryStrategy = SpiFactory.getInstance(RetryStrategy.class, RpcApplication.getRpcConfig().getRetryStrategy());
        try {
            ServiceMetaInfo finalServiceMetaInfo = serviceMetaInfo;
            rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.request(rpcRequest, finalServiceMetaInfo)
            );
        } catch (Exception ex) {
            // 第二次调用错误，直接抛出
            throw new RuntimeException("容错转移策略-再次调用错误", ex);
        }
        return rpcResponse;
    }
}
