package com.chord.myrpc.protocol;

import com.chord.myrpc.serializer.Serializer;
import com.chord.myrpc.serializer.SerializerFactory;
import com.chord.myrpc.spi.SpiFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议消息编码器
 */
public class ProtocolMessageEncoder {

    /**
     * 编码
     * @param protocolMessage
     * @return
     * @throws IOException
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        // 消息为空的情况
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();

        // 依次向缓冲区写入字节
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte((byte) (header.getSerializer() << 4 | header.getType()));
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());

        // 获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化器不存在");
        }
        Serializer serializer = SpiFactory.getInstance(Serializer.class, serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());

        // 写入 body 长度和数据
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;

    }
}
