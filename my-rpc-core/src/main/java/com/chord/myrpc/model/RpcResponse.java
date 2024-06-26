package com.chord.myrpc.model;

import com.chord.myrpc.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * RPC 响应
 * <p>封装调用方法得到的返回值、以及调用的信息（比如异常情况）等<p/>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {

    /**
     * 响应数据
     */
    private Object data;

    /**
     * 响应数据类型
     */
    private Class<?> dataType;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 是否得到了异常
     */
    private Boolean exception;

    public static RpcResponse failed() {
        return RpcResponse.builder()
                .message("failed")
                .build();
    }

    public static RpcResponse failed(String msg) {
        return RpcResponse.builder()
                .message(msg)
                .build();
    }
}
