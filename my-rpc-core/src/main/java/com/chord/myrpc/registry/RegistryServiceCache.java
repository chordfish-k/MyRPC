package com.chord.myrpc.registry;

import com.chord.myrpc.model.ServiceMetaInfo;

import java.util.List;

public class RegistryServiceCache implements KeyEventListener{

    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;

    /**
     * 写缓存
     * @param newServiceCache
     */
    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }

    /**
     * 读缓存
     * @return
     */
    List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }

    /**
     * 清空缓存
     */
    void clearCache() {
        this.serviceCache = null;
    }

    @Override
    public void notify(KeyEventType type) {
        if (type == KeyEventType.Delete || type == KeyEventType.Change) {
            clearCache();
        }
    }
}
