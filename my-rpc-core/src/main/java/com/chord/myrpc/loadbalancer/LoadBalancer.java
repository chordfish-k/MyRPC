package com.chord.myrpc.loadbalancer;

import com.chord.myrpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 消费端-负载均衡器
 */
public interface LoadBalancer {

    /**
     * 选择服务调用
     * @param requestParams
     * @param serviceMetaInfoList
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
