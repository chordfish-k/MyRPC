package com.chord.myrpc.fault.tolerant;

import com.chord.myrpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略 - 快速失败，遇到异常就外抛给外层处理
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务报错", e);
    }
}
