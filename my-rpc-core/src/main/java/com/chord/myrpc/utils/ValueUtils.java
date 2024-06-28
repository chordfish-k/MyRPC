package com.chord.myrpc.utils;

import java.lang.reflect.Field;

public class ValueUtils {

    /**
     * 生成指定类型的默认值对象
     * @param type
     * @return
     */
    public static Object getDefaultObject(Class<?> type) {
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
