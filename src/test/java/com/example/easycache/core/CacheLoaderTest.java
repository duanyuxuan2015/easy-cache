package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CacheLoader 缓存加载器单元测试
 * <p>测试缓存加载器的加载功能</p>
 */
public class CacheLoaderTest {

    @Test
    void testLoadSingleValue() throws Throwable {
        // 测试加载单个值
        CacheLoader<String, String> loader = key -> "value:" + key;

        String result = loader.load("key1");

        assertEquals("value:key1", result);
    }

    @Test
    void testLoadWithNullKey() throws Throwable {
        // 测试加载null键
        CacheLoader<String, String> loader = key -> key == null ? "null" : "value:" + key;

        String result = loader.load(null);

        assertEquals("null", result);
    }

    @Test
    void testLoadWithEmptyKey() throws Throwable {
        // 测试加载空键
        CacheLoader<String, String> loader = key -> "value:" + key;

        String result = loader.load("");

        assertEquals("value:", result);
    }

    @Test
    void testLoadWithChineseCharacters() throws Throwable {
        // 测试加载中文字符键
        CacheLoader<String, String> loader = key -> "值:" + key;

        String result = loader.load("测试");

        assertEquals("值:测试", result);
    }

    @Test
    void testLoadWithSpecialCharacters() throws Throwable {
        // 测试加载特殊字符键
        CacheLoader<String, String> loader = key -> "value:" + key;

        String result = loader.load("key:with:special:chars");

        assertEquals("value:key:with:special:chars", result);
    }

    @Test
    void testLoadAllDefaultImplementation() throws Throwable {
        // 测试默认批量加载实现
        CacheLoader<String, String> loader = key -> "value:" + key;

        Set<String> keys = Set.of("key1", "key2", "key3");

        Map<String, String> result = loader.loadAll(keys);

        assertEquals(3, result.size());
        assertEquals("value:key1", result.get("key1"));
        assertEquals("value:key2", result.get("key2"));
        assertEquals("value:key3", result.get("key3"));
    }

    @Test
    void testLoadAllWithEmptySet() throws Throwable {
        // 测试批量加载空集合
        CacheLoader<String, String> loader = key -> "value:" + key;

        Set<String> keys = Set.of();

        Map<String, String> result = loader.loadAll(keys);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testLoadAllWithSingleKey() throws Throwable {
        // 测试批量加载单个键
        CacheLoader<String, String> loader = key -> "value:" + key;

        Set<String> keys = Set.of("key1");

        Map<String, String> result = loader.loadAll(keys);

        assertEquals(1, result.size());
        assertEquals("value:key1", result.get("key1"));
    }

    @Test
    void testLoadAllWithCustomImplementation() throws Throwable {
        // 测试自定义批量加载实现
        CacheLoader<String, String> loader = new CacheLoader<String, String>() {
            @Override
            public String load(String key) throws Throwable {
                return "value:" + key;
            }

            @Override
            public Map<String, String> loadAll(Set<String> keys) throws Throwable {
                Map<String, String> map = new HashMap<>();
                for (String key : keys) {
                    map.put(key, "batch:" + key);
                }
                return map;
            }
        };

        Set<String> keys = Set.of("key1", "key2");

        Map<String, String> result = loader.loadAll(keys);

        assertEquals(2, result.size());
        assertEquals("batch:key1", result.get("key1"));
        assertEquals("batch:key2", result.get("key2"));
    }

    @Test
    void testLoadWithException() {
        // 测试加载时抛出异常
        CacheLoader<String, String> loader = key -> {
            throw new RuntimeException("Load failed for key: " + key);
        };

        assertThrows(RuntimeException.class, () -> loader.load("key1"));
    }

    @Test
    void testLoadWithComplexValue() throws Throwable {
        // 测试加载复杂值对象
        CacheLoader<String, TestObject> loader = key -> new TestObject(key, key.length());

        TestObject result = loader.load("test");

        assertEquals("test", result.getName());
        assertEquals(4, result.getAge());
    }

    @Test
    void testLoadWithNumericKey() throws Throwable {
        // 测试数字键
        CacheLoader<Integer, String> loader = key -> "value:" + key;

        String result = loader.load(123);

        assertEquals("value:123", result);
    }

    @Test
    void testLoadWithLongRunningOperation() throws Throwable {
        // 测试加载耗时操作
        CacheLoader<String, String> loader = key -> {
            Thread.sleep(100);
            return "value:" + key;
        };

        long start = System.currentTimeMillis();
        String result = loader.load("key1");
        long duration = System.currentTimeMillis() - start;

        assertEquals("value:key1", result);
        assertTrue(duration >= 100);
    }

    @Test
    void testLoadWithNullValue() throws Throwable {
        // 测试加载null值
        CacheLoader<String, String> loader = key -> null;

        String result = loader.load("key1");

        assertNull(result);
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
