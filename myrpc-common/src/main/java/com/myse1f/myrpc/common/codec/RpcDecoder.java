/**
 * Created By Yufan Wu
 * 2021/7/15
 */
package com.myse1f.myrpc.common.codec;

import com.myse1f.myrpc.common.serializer.Serializer;
import com.myse1f.myrpc.common.util.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

    /**
     * class to be decoded
     */
    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {
        /**
         * message format
         * | 1B | serialize version
         * | 4B | body length
         * | length | body
         */
        if (in.readableBytes() < 5){
            return ;
        }
        // 标记当前readIndex的位置，以便后面重置 readIndex 的时候使用
        in.markReaderIndex();
        // 读取序列化版本
        int serializeVersion = in.readByte();
        // 读取消息体（消息的长度）. readInt 操作会增加 readerIndex
        int dataLength = in.readInt();
        // 如果可读字节数小于消息长度，说明是不完整的消息
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        // 开始反序列化
        byte[] body = new byte[dataLength];
        in.readBytes(body);
        Serializer serializer = SerializerFactory.getSerializer(serializeVersion);
        Object obj = serializer.deserialize(body, genericClass);
        out.add(obj);
    }
}
