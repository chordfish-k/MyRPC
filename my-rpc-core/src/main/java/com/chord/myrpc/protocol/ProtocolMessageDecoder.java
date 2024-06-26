package com.chord.myrpc.protocol;

import cn.hutool.core.util.ByteUtil;
import com.chord.myrpc.model.RpcRequest;
import com.chord.myrpc.model.RpcResponse;
import com.chord.myrpc.serializer.Serializer;
import com.chord.myrpc.serializer.SerializerFactory;
import com.chord.myrpc.spi.SpiFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议消息解码器
 */
public class ProtocolMessageDecoder {

    /**
     * 解码
     * @param buffer
     * @return
     * @throws IOException
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        // 从指定位置读出对应字段
        // 魔数
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("消息 magic number 非法");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer((byte) (buffer.getByte(2) >> 4));
        header.setType((byte) ((byte)(buffer.getByte(2) << 4) >> 4));
        header.setStatus(buffer.getByte(3));
        header.setRequestId(buffer.getLong(4));
        header.setBodyLength(buffer.getInt(12));

        // 解决粘包问题，只读指定长度的数据
        byte[] bodyBytes = buffer.getBytes(ProtocolConstant.MESSAGE_HEADER_LENGTH,
                ProtocolConstant.MESSAGE_HEADER_LENGTH + header.getBodyLength());

        // 解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("消息指定的序列化器不存在");
        }
        Serializer serializer = SpiFactory.getInstance(Serializer.class, serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("消息指定的类型不存在");
        }
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }
    }
}
