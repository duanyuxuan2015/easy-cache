package com.example.easycache.core;

import org.caffinitas.ohc.CacheSerializer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * OHC缓存键序列化器
 * <p>将字符串键序列化为字节数组</p>
 */
public class OhcCacheKeySerializer implements CacheSerializer<String> {

    /**
     * 序列化字符串
     *
     * @param value 字符串值
     * @param buf 字节缓冲区
     */
    public void serialize(String value, ByteBuffer buf) {
        // 得到字符串对象UTF-8编码的字节数组
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        // 用前16位记录数组长度
        buf.put((byte) ((bytes.length >>> 8) & 0xFF));
        buf.put((byte) ((bytes.length) & 0xFF));
        buf.put(bytes);
    }

    /**
     * 反序列化字符串
     *
     * @param buf 字节缓冲区
     * @return 字符串值
     */
    @Override
    public String deserialize(ByteBuffer buf) {
        // 判断字节数组的长度
        int length = (((buf.get() & 0xff) << 8) + ((buf.get() & 0xff)));
        byte[] bytes = new byte[length];
        // 读取字节数组
        buf.get(bytes);
        // 返回字符串对象
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 计算序列化后的大小
     *
     * @param value 字符串值
     * @return 序列化后的大小
     */
    public int serializedSize(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        // 设置字符串长度限制，2^16 = 65536
        if (bytes.length > 65536)
            throw new RuntimeException("encoded string too long: " + bytes.length + " bytes");
        // 设置字符串长度限制，2^16 = 65536
        return bytes.length + 2;
    }
}
