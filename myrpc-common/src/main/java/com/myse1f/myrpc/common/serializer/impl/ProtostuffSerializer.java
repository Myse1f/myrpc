/**
 * Created By Yufan Wu
 * 2021/7/18
 */
package com.myse1f.myrpc.common.serializer.impl;

import com.myse1f.myrpc.common.serializer.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffSerializer implements Serializer {

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();
    private static Objenesis objenesis = new ObjenesisStd(true);

    public static ProtostuffSerializer getInstance() {
        return ProtostuffSerializerHolder.instance;
    }

    private ProtostuffSerializer() {

    }

    private static <T> Schema<T> getSchema(Class<T> klass) {
        Schema<T> schema = (Schema<T>) cachedSchema.computeIfAbsent(klass, RuntimeSchema::createFrom);
        return schema;
    }

    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> klass = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(klass);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> klass) {
        try {
            T message = objenesis.newInstance(klass);
            Schema<T> schema = getSchema(klass);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static class ProtostuffSerializerHolder {
        private static ProtostuffSerializer instance = new ProtostuffSerializer();
    }
}
