package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kryo5ValueEncoder 单元测试
 * <p>测试Kryo5值编码器的序列化功能</p>
 */
public class Kryo5ValueEncoderTest {

    @Test
    void testEncodeString() {
        // 测试字符串编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        String testValue = "Hello, World!";
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeInteger() {
        // 测试整数编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        Integer testValue = 12345;
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeLong() {
        // 测试长整数编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        Long testValue = 123456789L;
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeDouble() {
        // 测试双精度浮点数编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        Double testValue = 3.14159;
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeBoolean() {
        // 测试布尔值编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        Boolean testValue = true;
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeList() {
        // 测试列表编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        List<String> testValue = new ArrayList<>();
        testValue.add("item1");
        testValue.add("item2");
        testValue.add("item3");
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeMap() {
        // 测试Map编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        Map<String, Integer> testValue = new HashMap<>();
        testValue.put("key1", 100);
        testValue.put("key2", 200);
        testValue.put("key3", 300);
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeNull() {
        // 测试null值编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        byte[] result = encoder.apply(null);

        assertNotNull(result);
    }

    @Test
    void testEncodeEmptyString() {
        // 测试空字符串编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        String testValue = "";
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
    }

    @Test
    void testEncodeEmptyList() {
        // 测试空列表编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        List<String> testValue = new ArrayList<>();
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
    }

    @Test
    void testEncodeEmptyMap() {
        // 测试空Map编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        Map<String, String> testValue = new HashMap<>();
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
    }

    @Test
    void testEncodeComplexObject() {
        // 测试复杂对象编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        // 创建一个测试对象
        Map<String, Object> testValue = new HashMap<>();
        testValue.put("name", "Test");
        testValue.put("age", 25);
        testValue.put("active", true);
        testValue.put("score", 95.5);

        List<String> hobbies = new ArrayList<>();
        hobbies.add("reading");
        hobbies.add("coding");
        testValue.put("hobbies", hobbies);

        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeChineseCharacters() {
        // 测试中文字符编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        String testValue = "你好，世界！";
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testEncodeLargeString() {
        // 测试大字符串编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
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
    void testEncodeSpecialCharacters() {
        // 测试特殊字符编码
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;
        String testValue = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        byte[] result = encoder.apply(testValue);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
