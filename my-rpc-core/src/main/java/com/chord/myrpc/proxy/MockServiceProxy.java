package com.chord.myrpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Mock服务代理 (JDK动态代理)
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 根据方法的返回类型，生成特定的默认值对象
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    /**
     * 生成指定类型的默认值对象
     * @param type
     * @return
     */
    private Object getDefaultObject(Class<?> type) {
        // 基本类型
        if (type.isPrimitive()) {
            if (type.equals(boolean.class)) {
                return false;
            } else if (type.equals(char.class)) {
                return '*';
            } else if (type.equals(short.class)) {
                return (short)0;
            } else if (type.equals(int.class)) {
                return 0;
            } else if (type.equals(long.class)) {
                return 0L;
            }
        } else if (type.equals(String.class)) {
            return "mockStr";
        }
        // 对象类型
        Object res;
        try {
            res = type.getDeclaredConstructor().newInstance();
            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(res, getDefaultObject(field.getType()));
            }
        } catch (Exception e) {
            return null;
        }
        return res;
    }
}
