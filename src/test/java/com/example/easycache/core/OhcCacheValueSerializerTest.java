package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OhcCacheValueSerializer OHC缓存值序列化器单元测试
 */
public class OhcCacheValueSerializerTest {

    private final OhcCacheValueSerializer serializer = new OhcCacheValueSerializer();

    @Test
    void testSerializeAndDeserializeBytes() {
        // 测试字节数组序列化和反序列化
        byte[] original = "testValue".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        serializer.serialize(original, buffer);
        buffer.flip();

        byte[] deserialized = serializer.deserialize(buffer);

        assertArrayEquals(original, deserialized);
    }

    @Test
    void testSerializeEmptyArray() {
        // 测试空数组序列化
        byte[] original = new byte[0];
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        serializer.serialize(original, buffer);
        buffer.flip();

        byte[] deserialized = serializer.deserialize(buffer);

        assertArrayEquals(original, deserialized);
    }

    @Test
    void testSerializeLargeArray() {
        // 测试大数组序列化
        byte[] original = new byte[10000];
        for (int i = 0; i < original.length; i++) {
            original[i] = (byte) (i % 256);
        }
        ByteBuffer buffer = ByteBuffer.allocate(20000);

        serializer.serialize(original, buffer);
        buffer.flip();

        byte[] deserialized = serializer.deserialize(buffer);

        assertArrayEquals(original, deserialized);
    }

    @Test
    void testSerializedSize() {
        // 测试序列化大小计算
        byte[] original = new byte[100];

        int size = serializer.serializedSize(original);

        assertEquals(original.length + 4, size); // 4 bytes for length
    }

    @Test
    void testSerializedSizeForEmptyArray() {
        // 测试空数组的序列化大小
        byte[] original = new byte[0];

        int size = serializer.serializedSize(original);

        assertEquals(4, size); // 4 bytes for length
    }

    @Test
    void testSerializeWithAllByteValues() {
        // 测试所有字节值的序列化
        byte[] original = new byte[256];
        for (int i = 0; i < 256; i++) {
            original[i] = (byte) i;
        }
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        serializer.serialize(original, buffer);
        buffer.flip();

        byte[] deserialized = serializer.deserialize(buffer);

        assertArrayEquals(original, deserialized);
    }

    @Test
    void testMultipleSerializeDeserialize() {
        // 测试多次序列化和反序列化
        byte[][] arrays = {
            "test1".getBytes(),
            "test2".getBytes(),
            new byte[0],
            new byte[100],
            "中文测试".getBytes()
        };
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        for (byte[] array : arrays) {
            buffer.clear();
            serializer.serialize(array, buffer);
            buffer.flip();

            byte[] deserialized = serializer.deserialize(buffer);
            assertArrayEquals(array, deserialized);
        }
    }

    @Test
    void testSerializeWithBufferPosition() {
        // 测试带有缓冲区位置的序列化
        byte[] original = "testValue".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 先写入一些数据
        buffer.putInt(12345);

        serializer.serialize(original, buffer);
        buffer.flip();

        // 跳过之前的数据
        buffer.getInt();
        byte[] deserialized = serializer.deserialize(buffer);

        assertArrayEquals(original, deserialized);
    }

    @Test
    void testSerializeWithChineseCharacters() {
        // 测试中文字符序列化
        byte[] original = "测试值".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        serializer.serialize(original, buffer);
        buffer.flip();

        byte[] deserialized = serializer.deserialize(buffer);

        assertArrayEquals(original, deserialized);
    }

    @Test
    void testDeserializeEmptyBuffer() {
        // 测试反序列化空缓冲区会抛出异常
        ByteBuffer buffer = ByteBuffer.allocate(0);

        assertThrows(java.nio.BufferUnderflowException.class, () -> {
            serializer.deserialize(buffer);
        });
    }

    @Test
    void testSerializeConsistency() {
        // 测试序列化一致性
        byte[] original = "consistent data".getBytes();
        ByteBuffer buffer1 = ByteBuffer.allocate(1024);
        ByteBuffer buffer2 = ByteBuffer.allocate(1024);

        serializer.serialize(original, buffer1);
        serializer.serialize(original, buffer2);

        buffer1.flip();
        buffer2.flip();

        byte[] deserialized1 = serializer.deserialize(buffer1);
        byte[] deserialized2 = serializer.deserialize(buffer2);

        assertArrayEquals(deserialized1, deserialized2);
        assertArrayEquals(original, deserialized1);
    }
}
