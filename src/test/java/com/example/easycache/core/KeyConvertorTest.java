package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KeyConvertor 键转换器接口单元测试
 * <p>测试键转换器的接口规范</p>
 */
public class KeyConvertorTest {

    @Test
    void testKeyConvertorInterface() {
        // 测试键转换器接口实现
        KeyConvertor<String> convertor = (cacheName, key) -> cacheName + ":" + key;

        String result = convertor.apply("testCache", "myKey");

        assertEquals("testCache:myKey", result);
    }

    @Test
    void testKeyConvertorWithIntegerKey() {
        // 测试整数键转换
        KeyConvertor<Integer> convertor = (cacheName, key) -> cacheName + ":" + key;

        String result = convertor.apply("testCache", 12345);

        assertEquals("testCache:12345", result);
    }

    @Test
    void testKeyConvertorWithNullKey() {
        // 测试null键转换
        KeyConvertor<String> convertor = (cacheName, key) -> {
            if (key == null) {
                return cacheName + ":null";
            }
            return cacheName + ":" + key;
        };

        String result = convertor.apply("testCache", null);

        assertEquals("testCache:null", result);
    }

    @Test
    void testKeyConvertorWithEmptyKey() {
        // 测试空键转换
        KeyConvertor<String> convertor = (cacheName, key) -> cacheName + ":" + key;

        String result = convertor.apply("testCache", "");

        assertEquals("testCache:", result);
    }

    @Test
    void testKeyConvertorWithComplexObject() {
        // 测试复杂对象键转换
        class TestKey {
            private final String id;
            private final int type;

            public TestKey(String id, int type) {
                this.id = id;
                this.type = type;
            }

            @Override
            public String toString() {
                return id + ":" + type;
            }
        }

        KeyConvertor<TestKey> convertor = (cacheName, key) -> cacheName + ":" + key.toString();

        TestKey key = new TestKey("ABC", 1);
        String result = convertor.apply("testCache", key);

        assertEquals("testCache:ABC:1", result);
    }

    @Test
    void testKeyConvertorConsistency() {
        // 测试转换器的一致性
        KeyConvertor<String> convertor = (cacheName, key) -> cacheName + ":" + key;

        String result1 = convertor.apply("cache1", "key1");
        String result2 = convertor.apply("cache1", "key1");

        assertEquals(result1, result2);
    }

    @Test
    void testKeyConvertorWithDifferentCacheNames() {
        // 测试不同缓存名称
        KeyConvertor<String> convertor = (cacheName, key) -> cacheName + ":" + key;

        String result1 = convertor.apply("cache1", "key1");
        String result2 = convertor.apply("cache2", "key1");

        assertNotEquals(result1, result2);
        assertEquals("cache1:key1", result1);
        assertEquals("cache2:key1", result2);
    }

    @Test
    void testKeyConvertorWithSpecialCharacters() {
        // 测试特殊字符键
        KeyConvertor<String> convertor = (cacheName, key) -> cacheName + ":" + key;

        String result = convertor.apply("testCache", "key:with:special:chars");

        assertEquals("testCache:key:with:special:chars", result);
    }

    @Test
    void testKeyConvertorWithChineseCharacters() {
        // 测试中文字符键
        KeyConvertor<String> convertor = (cacheName, key) -> cacheName + ":" + key;

        String result = convertor.apply("testCache", "测试键");

        assertEquals("testCache:测试键", result);
    }
}
