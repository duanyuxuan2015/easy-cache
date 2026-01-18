package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CacheConfigBuilder 单元测试
 * <p>测试缓存配置构建器的各种配置项</p>
 */
public class CacheConfigBuilderTest {

    @Test
    void testBuildBasicConfig() {
        // 测试基本配置构建
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .build();

        assertNotNull(config);
        assertEquals("testCache", config.getName());
    }

    @Test
    void testBuildWithExpire() {
        // 测试设置过期时间
        Duration expire = Duration.ofMinutes(10);
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .expire(expire)
                .build();

        assertEquals(expire, config.getExpire());
    }

    @Test
    void testBuildWithLocalExpire() {
        // 测试设置本地缓存过期时间
        Duration localExpire = Duration.ofSeconds(30);
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localExpire(localExpire)
                .build();

        assertEquals(localExpire, config.getLocalExpire());
    }

    @Test
    void testBuildWithLocalLimit() {
        // 测试设置本地缓存最大条目数
        Integer localLimit = 5000;
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(localLimit)
                .build();

        assertEquals(localLimit, config.getLocalLimit());
    }

    @Test
    void testBuildWithCacheType() {
        // 测试设置缓存类型
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .cacheType(CacheType.BOTH)
                .build();

        assertEquals(CacheType.BOTH, config.getCacheType());
    }

    @Test
    void testBuildWithSyncLocal() {
        // 测试设置是否同步本地缓存
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .syncLocal(true)
                .build();

        assertTrue(config.getSyncLocal());
    }

    @Test
    void testBuildWithNullValueExpire() {
        // 测试设置空值过期时间
        Duration nullValueExpire = Duration.ofSeconds(60);
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .nullValueExpire(nullValueExpire)
                .build();

        assertEquals(nullValueExpire, config.getNullValueExpire());
    }

    @Test
    void testBuildWithLoadLock() {
        // 测试设置加载锁
        Integer lockShards = 16;
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .loadLock(lockShards)
                .build();

        assertTrue(config.getLoadLock());
        assertEquals(lockShards, config.getLoadLockShards());
    }

    @Test
    void testBuildWithKeyConvertor() {
        // 测试设置键转换器
        KeyConvertor<String> keyConvertor = (cacheName, key) -> cacheName + ":" + key;
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .keyConvertor(keyConvertor)
                .build();

        assertEquals(keyConvertor, config.getKeyConvertor());
    }

    @Test
    void testBuildWithOffHeap() {
        // 测试设置是否使用堆外内存
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .offHeap(true)
                .build();

        assertTrue(config.getOffHeap());
    }

    @Test
    void testBuildWithBufReleaseDelay() {
        // 测试设置缓冲区释放延迟
        Long delay = 3000L;
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .bufReleaseDelay(delay)
                .build();

        assertEquals(delay, config.getBufReleaseDelay());
    }

    @Test
    void testBuildWithLocalExpireType() {
        // 测试设置本地缓存过期类型
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localExpireType(ExpireType.AFTER_ACCESS)
                .build();

        assertEquals(ExpireType.AFTER_ACCESS, config.getLocalExpireType());
    }

    @Test
    void testBuildWithMultipleOptions() {
        // 测试同时设置多个配置项
        Duration expire = Duration.ofMinutes(5);
        Duration localExpire = Duration.ofSeconds(30);
        Integer localLimit = 1000;
        KeyConvertor<String> keyConvertor = (cacheName, key) -> cacheName + ":" + key;

        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .expire(expire)
                .localExpire(localExpire)
                .localLimit(localLimit)
                .cacheType(CacheType.BOTH)
                .syncLocal(true)
                .keyConvertor(keyConvertor)
                .build();

        assertEquals("testCache", config.getName());
        assertEquals(expire, config.getExpire());
        assertEquals(localExpire, config.getLocalExpire());
        assertEquals(localLimit, config.getLocalLimit());
        assertEquals(CacheType.BOTH, config.getCacheType());
        assertTrue(config.getSyncLocal());
        assertEquals(keyConvertor, config.getKeyConvertor());
    }

    @Test
    void testBuildWithDefaultValues() {
        // 测试默认值
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .build();

        // 验证默认值
        assertNotNull(config.getValueEncoder());
        assertNotNull(config.getValueDecoder());
        assertEquals(ExpireType.AFTER_WRITE, config.getLocalExpireType());
        assertEquals(LocalCacheType.CAFFEINE, config.getLocalCacheType());
        assertFalse(config.getLoadLock());
        assertFalse(config.getOffHeap());
        assertEquals(5000L, config.getBufReleaseDelay());
    }

    @Test
    void testBuildWithCacheLoader() {
        // 测试设置缓存加载器
        CacheLoader<String, String> loader = key -> "value:" + key;
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .loader(loader)
                .build();

        assertEquals(loader, config.getLoader());
    }

    @Test
    void testBuildWithCacheNullValue() {
        // 测试设置空值对象
        String nullValue = "NULL";
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .cacheNullValue(nullValue)
                .build();

        assertEquals(nullValue, config.getCacheNullValue());
    }

    @Test
    void testBuildWithCustomEncoder() {
        // 测试设置自定义编码器
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .valueEncoder(value -> "custom".getBytes())
                .build();

        assertNotNull(config.getValueEncoder());
    }

    @Test
    void testBuildWithCustomDecoder() {
        // 测试设置自定义解码器
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .valueDecoder(bytes -> "custom")
                .build();

        assertNotNull(config.getValueDecoder());
    }
}
