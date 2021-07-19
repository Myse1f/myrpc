/**
 * Created By Yufan Wu
 * 2021/7/18
 */
package com.myse1f.myrpc.common.util;

import com.myse1f.myrpc.common.serializer.Serializer;
import com.myse1f.myrpc.common.serializer.impl.ProtostuffSerializer;

public class SerializerFactory {
    public static Serializer getSerializer(int version) {
        Serializer res;
        switch (version) {
            case 0: res = ProtostuffSerializer.getInstance(); break;
            default: throw new IllegalStateException();
        }
        return res;
    }
}
