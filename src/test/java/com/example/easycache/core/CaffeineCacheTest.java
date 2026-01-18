package com.example.easycache.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CaffeineCache 单元测试
 * <p>测试Caffeine本地缓存实现的功能</p>
 */
public class CaffeineCacheTest {

    private CacheConfig<String, String> config;
    private CaffeineCache<String, String> cache;

    @BeforeEach
    void setUp() {
        // 初始化配置和缓存实例
        config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .localExpire(Duration.ofMinutes(10))
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();
        cache = new CaffeineCache<>(config);
    }

    @Test
    void testPutAndGet() {
        // 测试基本的put和get操作
        cache.put("key1", "value1");
        String value = cache.get("key1");

        assertEquals("value1", value);
    }

    @Test
    void testGetNonExistentKey() {
        // 测试获取不存在的键
        String value = cache.get("nonExistent");

        assertNull(value);
    }

    @Test
    void testPutNull() {
        // 测试存储null值
        cache.put("key1", null);
        String value = cache.get("key1");

        assertNull(value);
    }

    @Test
    void testPutOverwrite() {
        // 测试覆盖已存在的值
        cache.put("key1", "value1");
        cache.put("key1", "value2");

        String value = cache.get("key1");
        assertEquals("value2", value);
    }

    @Test
    void testRemove() {
        // 测试移除键
        cache.put("key1", "value1");
        boolean removed = cache.remove("key1");

        assertTrue(removed);
        assertNull(cache.get("key1"));
    }

    @Test
    void testRemoveNonExistentKey() {
        // 测试移除不存在的键
        boolean removed = cache.remove("nonExistent");

        assertFalse(removed);
    }

    @Test
    void testPutAll() {
        // 测试批量存储
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");

        cache.putAll(map);

        assertEquals("value1", cache.get("key1"));
        assertEquals("value2", cache.get("key2"));
        assertEquals("value3", cache.get("key3"));
    }

    @Test
    void testGetAll() {
        // 测试批量获取
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        Set<String> keys = new HashSet<>();
        keys.add("key1");
        keys.add("key2");
        keys.add("key3");

        Map<String, String> result = cache.getAll(keys);

        assertEquals(3, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
        assertEquals("value3", result.get("key3"));
    }

    @Test
    void testGetAllWithPartialMatch() {
        // 测试批量获取部分匹配
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        Set<String> keys = new HashSet<>();
        keys.add("key1");
        keys.add("key2");
        keys.add("key3"); // 不存在的键

        Map<String, String> result = cache.getAll(keys);

        assertEquals(3, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
        assertNull(result.get("key3"));
    }

    @Test
    void testRemoveAll() {
        // 测试批量移除
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        Set<String> keys = new HashSet<>();
        keys.add("key1");
        keys.add("key2");

        cache.removeAll(keys);

        assertNull(cache.get("key1"));
        assertNull(cache.get("key2"));
        assertEquals("value3", cache.get("key3"));
    }

    @Test
    void testConfig() {
        // 测试获取配置
        CacheConfig<String, String> retrievedConfig = cache.config();

        assertNotNull(retrievedConfig);
        assertEquals("testCache", retrievedConfig.getName());
    }

    @Test
    void testWithKeyConvertor() {
        // 测试带键转换器的缓存
        CacheConfig<String, String> configWithConverter = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .keyConvertor((cacheName, key) -> cacheName + ":" + key)
                .build();

        CaffeineCache<String, String> cacheWithConverter = new CaffeineCache<>(configWithConverter);

        cacheWithConverter.put("key1", "value1");
        String value = cacheWithConverter.get("key1");

        assertEquals("value1", value);
    }

    @Test
    void testCacheExpiration() throws InterruptedException {
        // 测试缓存过期
        CacheConfig<String, String> expireConfig = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .localExpire(Duration.ofMillis(100))
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineCache<String, String> expireCache = new CaffeineCache<>(expireConfig);

        expireCache.put("key1", "value1");
        assertEquals("value1", expireCache.get("key1"));

        // 等待过期
        Thread.sleep(150);

        assertNull(expireCache.get("key1"));
    }

    @Test
    void testCacheWithNullValue() {
        // 测试缓存空值对象
        String nullValue = "NULL";
        CacheConfig<String, String> configWithNull = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .cacheNullValue(nullValue)
                .nullValueExpire(Duration.ofMillis(100))
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineCache<String, String> cacheWithNull = new CaffeineCache<>(configWithNull);

        cacheWithNull.put("key1", null);
        assertEquals(nullValue, cacheWithNull.get("key1"));
    }

    @Test
    void testLargeData() {
        // 测试存储大量数据
        for (int i = 0; i < 100; i++) {
            cache.put("key" + i, "value" + i);
        }

        for (int i = 0; i < 100; i++) {
            assertEquals("value" + i, cache.get("key" + i));
        }
    }

    @Test
    void testUpdateAfterAccess() throws InterruptedException {
        // 测试访问后更新策略
        CacheConfig<String, String> accessConfig = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .localExpire(Duration.ofMillis(200))
                .localExpireType(ExpireType.AFTER_ACCESS)
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineCache<String, String> accessCache = new CaffeineCache<>(accessConfig);

        accessCache.put("key1", "value1");

        // 多次访问，每次访问都重置过期时间
        for (int i = 0; i < 3; i++) {
            Thread.sleep(80);
            assertNotNull(accessCache.get("key1"));
        }
    }

    @Test
    void testSpecialCharactersInKey() {
        // 测试特殊字符键
        String specialKey = "key:with:special:chars";
        cache.put(specialKey, "value");

        assertEquals("value", cache.get(specialKey));
    }

    @Test
    void testChineseCharacters() {
        // 测试中文字符
        cache.put("键1", "值1");
        cache.put("键2", "值2");

        assertEquals("值1", cache.get("键1"));
        assertEquals("值2", cache.get("键2"));
    }
}
