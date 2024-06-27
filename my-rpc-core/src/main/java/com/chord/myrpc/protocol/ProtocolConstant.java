package com.chord.myrpc.protocol;

/**
 * 协议常量
 */
public interface ProtocolConstant {

    /**
     * 消息头长度：1+1+1+1+1+8+4=17
     */
    int MESSAGE_HEADER_LENGTH = 17;

    /**
     * 协议魔数
     */
    byte PROTOCOL_MAGIC = 0x17;

    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}