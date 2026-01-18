package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OhcCacheKeySerializer OHC缓存键序列化器单元测试
 */
public class OhcCacheKeySerializerTest {

    private final OhcCacheKeySerializer serializer = new OhcCacheKeySerializer();

    @Test
    void testSerializeAndDeserializeString() {
        // 测试字符串序列化和反序列化
        String original = "testKey";
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        serializer.serialize(original, buffer);
        buffer.flip();

        String deserialized = serializer.deserialize(buffer);

        assertEquals(original, deserialized);
    }

    @Test
    void testSerializeEmptyString() {
        // 测试空字符串序列化
        String original = "";
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        serializer.serialize(original, buffer);
        buffer.flip();

        String deserialized = serializer.deserialize(buffer);

        assertEquals(original, deserialized);
    }

    @Test
    void testSerializeChineseCharacters() {
        // 测试中文字符序列化
        String original = "测试键";
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        serializer.serialize(original, buffer);
        buffer.flip();

        String deserialized = serializer.deserialize(buffer);

        assertEquals(original, deserialized);
    }

    @Test
    void testSerializeSpecialCharacters() {
        // 测试特殊字符序列化
        String original = "key:with:special:chars!@#$%";
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        serializer.serialize(original, buffer);
        buffer.flip();

        String deserialized = serializer.deserialize(buffer);

        assertEquals(original, deserialized);
    }

    @Test
    void testSerializeLongString() {
        // 测试长字符串序列化
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("test");
        }
        String original = sb.toString();
        ByteBuffer buffer = ByteBuffer.allocate(10000);

        serializer.serialize(original, buffer);
        buffer.flip();

        String deserialized = serializer.deserialize(buffer);

        assertEquals(original, deserialized);
    }

    @Test
    void testSerializedSize() {
        // 测试序列化大小计算
        String original = "testKey";

        int size = serializer.serializedSize(original);

        assertEquals(original.getBytes().length + 2, size);
    }

    @Test
    void testSerializedSizeForEmptyString() {
        // 测试空字符串的序列化大小
        String original = "";

        int size = serializer.serializedSize(original);

        assertEquals(2, size); // 2 bytes for length
    }

    @Test
    void testSerializedSizeForChineseString() {
        // 测试中文字符串的序列化大小
        String original = "测试";

        int size = serializer.serializedSize(original);

        assertTrue(size > 0);
    }

    @Test
    void testMultipleSerializeDeserialize() {
        // 测试多次序列化和反序列化
        String[] keys = {"key1", "key2", "key3", "测试", "test:test"};
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        for (String key : keys) {
            buffer.clear();
            serializer.serialize(key, buffer);
            buffer.flip();

            String deserialized = serializer.deserialize(buffer);
            assertEquals(key, deserialized);
        }
    }

    @Test
    void testSerializeSizeLimit() {
        // 测试序列化大小限制
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 70000; i++) {
            sb.append("a");
        }
        String original = sb.toString();

        assertThrows(RuntimeException.class, () -> {
            serializer.serializedSize(original);
        });
    }

    @Test
    void testSerializeWithBufferPosition() {
        // 测试带有缓冲区位置的序列化
        String original = "testKey";
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 先写入一些数据
        buffer.putInt(12345);

        serializer.serialize(original, buffer);
        buffer.flip();

        // 跳过之前的数据
        buffer.getInt();
        String deserialized = serializer.deserialize(buffer);

        assertEquals(original, deserialized);
    }

    @Test
    void testDeserializeEmptyBuffer() {
        // 测试反序列化空缓冲区会抛出异常
        ByteBuffer buffer = ByteBuffer.allocate(0);

        assertThrows(java.nio.BufferUnderflowException.class, () -> {
            serializer.deserialize(buffer);
        });
    }
}
