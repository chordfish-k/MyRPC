package com.chord.myrpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 协议消息结构
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {

    /**
     * 消息头
     */
    private Header header;

    /**
     * 消息体
     */
    private T body;

    @Data
    public static class Header {

        /**
         * 魔数，用于判断是否该协议的请求 8b
         */
        private byte magic;

        /**
         * 版本号 8b
         */
        private byte version;

        /**
         * 序列化器编号 4b
         */
        private byte serializer;

        /**
         * 消息类型（请求 / 响应） 4b
         */
        private byte type;

        /**
         * 状态 8b
         */
        private byte status;

        /**
         * 请求id 64b
         */
        private long requestId;

        /**
         * 消息体长度 32b
         */
        private int bodyLength;
    }
}
