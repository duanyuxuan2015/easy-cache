package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KryoValueEncoder Kryo值编码器单元测试
 */
public class KryoValueEncoderTest {

    @Test
    void testEncodeString() {
        // 测试字符串编码
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;
        String testValue = "Hello, World!";
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeInteger() {
        // 测试整数编码
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;
        Integer testValue = 12345;
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeNull() {
        // 测试null值编码
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;
        byte[] result = encoder.apply(null);

        assertNotNull(result);
    }

    @Test
    void testEncodeList() {
        // 测试列表编码
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;
        java.util.List<String> testValue = java.util.Arrays.asList("item1", "item2", "item3");
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeMap() {
        // 测试Map编码
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;
        java.util.Map<String, Integer> testValue = new java.util.HashMap<>();
        testValue.put("key1", 100);
        testValue.put("key2", 200);
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeConsistency() {
        // 测试编码一致性
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;
        String testValue = "test";
        byte[] result1 = encoder.apply(testValue);
        byte[] result2 = encoder.apply(testValue);

        assertArrayEquals(result1, result2);
    }

    @Test
    void testSingletonInstance() {
        // 测试单例实例
        KryoValueEncoder instance1 = KryoValueEncoder.INSTANCE;
        KryoValueEncoder instance2 = KryoValueEncoder.INSTANCE;

        assertSame(instance1, instance2);
    }

    @Test
    void testEncodeLargeString() {
        // 测试大字符串编码
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("test");
        }
        String testValue = sb.toString();
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeChineseCharacters() {
        // 测试中文字符编码
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;
        String testValue = "你好，世界！";
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeSpecialCharacters() {
        // 测试特殊字符编码
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;
        String testValue = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
