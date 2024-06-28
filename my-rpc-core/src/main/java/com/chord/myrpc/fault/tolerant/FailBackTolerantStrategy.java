package com.chord.myrpc.fault.tolerant;

import com.chord.myrpc.model.RpcResponse;
import com.chord.myrpc.proxy.ServiceProxyFactory;
import com.chord.myrpc.utils.ValueUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 容错策略 - 服务降级
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 获取降级的服务并调用
        Callable<RpcResponse> failBackFunction = (Callable<RpcResponse>) context.getOrDefault("failBackFunction", null);
        if (failBackFunction != null) {
            try {
                return failBackFunction.call();
            } catch (Exception ex) {
                //
            }
        }

        Method method = (Method) context.getOrDefault("method", null);
        Object result = ValueUtils.getDefaultObject(method.getReturnType());
        return RpcResponse.builder()
                .data(result)
                .dataType(result == null ? null : result.getClass())
                .message("failBack")
                .build();
    }
}
