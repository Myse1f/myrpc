/**
 * Created By Yufan Wu
 * 2021/7/15
 */
package com.myse1f.myrpc.common.codec;

import com.myse1f.myrpc.common.serializer.Serializer;
import com.myse1f.myrpc.common.util.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;
    private int serializeVersion;

    public RpcEncoder(Class<?> genericClass, int serializeVersion) {
        this.genericClass = genericClass;
        this.serializeVersion = serializeVersion;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            Serializer serializer = SerializerFactory.getSerializer(serializeVersion);
            byte[] data = serializer.serialize(in);
            out.writeByte(serializeVersion);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
