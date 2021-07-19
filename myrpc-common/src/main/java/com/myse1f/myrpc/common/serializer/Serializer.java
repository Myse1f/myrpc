/**
 * Created By Yufan Wu
 * 2021/7/18
 */
package com.myse1f.myrpc.common.serializer;

/**
 * 设计成接口，方便扩展
 */
public interface Serializer {
    <T> byte[] serialize(T obj);
    <T> T deserialize(byte[] data, Class<T> klass);
}
