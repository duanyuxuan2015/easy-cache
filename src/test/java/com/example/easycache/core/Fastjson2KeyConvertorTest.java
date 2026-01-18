package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fastjson2KeyConvertor 单元测试
 * <p>测试Fastjson2键转换器的功能</p>
 */
public class Fastjson2KeyConvertorTest {

    @Test
    void testConvertStringKey() {
        // 测试字符串键转换
        Fastjson2KeyConvertor<String> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";
        String originalKey = "myKey";

        String result = convertor.apply(cacheName, originalKey);

        assertEquals(originalKey, result);
    }

    @Test
    void testConvertNullKey() {
        // 测试null键转换
        Fastjson2KeyConvertor<String> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";

        String result = convertor.apply(cacheName, null);

        assertNull(result);
    }

    @Test
    void testConvertIntegerKey() {
        // 测试整数键转换
        Fastjson2KeyConvertor<Integer> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";
        Integer originalKey = 12345;

        String result = convertor.apply(cacheName, originalKey);

        assertEquals("12345", result);
    }

    @Test
    void testConvertLongKey() {
        // 测试长整数键转换
        Fastjson2KeyConvertor<Long> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";
        Long originalKey = 123456789L;

        String result = convertor.apply(cacheName, originalKey);

        assertEquals("123456789", result);
    }

    @Test
    void testConvertDoubleKey() {
        // 测试双精度浮点数键转换
        Fastjson2KeyConvertor<Double> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";
        Double originalKey = 3.14159;

        String result = convertor.apply(cacheName, originalKey);

        assertEquals("3.14159", result);
    }

    @Test
    void testConvertBooleanKey() {
        // 测试布尔值键转换
        Fastjson2KeyConvertor<Boolean> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";
        Boolean originalKey = true;

        String result = convertor.apply(cacheName, originalKey);

        assertEquals("true", result);
    }

    @Test
    void testConvertObjectKey() {
        // 测试对象键转换
        Fastjson2KeyConvertor<TestObject> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";
        TestObject originalKey = new TestObject("test", 25);

        String result = convertor.apply(cacheName, originalKey);

        assertNotNull(result);
        assertTrue(result.contains("test"));
        assertTrue(result.contains("25"));
    }

    @Test
    void testConvertMapKey() {
        // 测试Map键转换
        Fastjson2KeyConvertor<Map<String, Object>> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";

        Map<String, Object> originalKey = new HashMap<>();
        originalKey.put("name", "Test");
        originalKey.put("age", 25);

        String result = convertor.apply(cacheName, originalKey);

        assertNotNull(result);
        assertTrue(result.contains("name"));
        assertTrue(result.contains("age"));
    }

    @Test
    void testConvertEmptyStringKey() {
        // 测试空字符串键转换
        Fastjson2KeyConvertor<String> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";
        String originalKey = "";

        String result = convertor.apply(cacheName, originalKey);

        assertEquals("", result);
    }

    @Test
    void testConvertNegativeNumberKey() {
        // 测试负数键转换
        Fastjson2KeyConvertor<Integer> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";
        Integer originalKey = -999;

        String result = convertor.apply(cacheName, originalKey);

        assertEquals("-999", result);
    }

    @Test
    void testConvertZeroKey() {
        // 测试零值键转换
        Fastjson2KeyConvertor<Integer> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";
        Integer originalKey = 0;

        String result = convertor.apply(cacheName, originalKey);

        assertEquals("0", result);
    }

    @Test
    void testConvertChineseStringKey() {
        // 测试中文字符串键转换
        Fastjson2KeyConvertor<String> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";
        String originalKey = "测试键";

        String result = convertor.apply(cacheName, originalKey);

        assertEquals(originalKey, result);
    }

    @Test
    void testSingletonInstance() {
        // 测试单例实例
        Fastjson2KeyConvertor<?> instance1 = Fastjson2KeyConvertor.INSTANCE;
        Fastjson2KeyConvertor<?> instance2 = Fastjson2KeyConvertor.INSTANCE;

        assertSame(instance1, instance2);
    }

    @Test
    void testConsistentConversion() {
        // 测试转换的一致性
        Fastjson2KeyConvertor<TestObject> convertor = new Fastjson2KeyConvertor<>();
        String cacheName = "testCache";
        TestObject originalKey = new TestObject("test", 25);

        String result1 = convertor.apply(cacheName, originalKey);
        String result2 = convertor.apply(cacheName, originalKey);

        assertEquals(result1, result2);
    }

    /**
     * 测试用的简单对象
     */
    static class TestObject {
        private String name;
        private int age;

        public TestObject(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}
