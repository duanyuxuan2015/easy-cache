package com.example.easycache.core;

import org.caffinitas.ohc.CacheSerializer;

import java.nio.ByteBuffer;

/**
 * OHC缓存值序列化器
 * <p>序列化字节数组</p>
 */
public class OhcCacheValueSerializer implements CacheSerializer<byte[]> {
    /**
     * 序列化字节数组
     *
     * @param bytes 字节数组
     * @param buf 字节缓冲区
     */
    @Override
    public void serialize(byte[] bytes, ByteBuffer buf) {
        buf.putInt(bytes.length);
        buf.put(bytes);
    }

    /**
     * 反序列化字节数组
     *
     * @param buf 字节缓冲区
     * @return 字节数组
     */
    public byte[] deserialize(ByteBuffer buf) {
        byte[] bytes = new byte[buf.getInt()];
        buf.get(bytes);
        return bytes;
    }

    /**
     * 计算序列化后的大小
     *
     * @param t 字节数组
     * @return 序列化后的大小
     */
    @Override
    public int serializedSize(byte[] t) {
        return t.length + 4;
    }
}
