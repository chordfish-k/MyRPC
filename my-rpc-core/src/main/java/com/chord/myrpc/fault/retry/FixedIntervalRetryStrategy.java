package com.chord.myrpc.fault.retry;

import com.chord.myrpc.model.RpcResponse;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 重试策略 - 固定间隔
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS)) // 间隔等待3秒
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // 最大尝试3次 TODO 可设置次数
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        long attemptNumber = attempt.getAttemptNumber();
                        if (attemptNumber > 1) {
                            log.info("重试次数 {}", attempt.getAttemptNumber()-1);
                        }
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
