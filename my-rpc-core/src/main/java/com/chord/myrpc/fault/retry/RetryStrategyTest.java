package com.chord.myrpc.fault.retry;

import org.junit.Test;

public class RetryStrategyTest {

    RetryStrategy retryStrategy = new FixedIntervalRetryStrategy();

    @Test
    public void doRetry() {
        try {
            retryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("模拟重试失败");
            });
        } catch (Exception e) {
            System.out.println("重试多次失败");
            e.printStackTrace();
        }
    }
}
