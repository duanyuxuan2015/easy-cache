package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CacheConfig 配置类单元测试
 * <p>测试缓存配置的各种属性</p>
 */
public class CacheConfigTest {

    @Test
    void testGetConfigName() {
        // 测试获取配置名称
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache").build();

        assertEquals("testCache", config.getName());
    }

    @Test
    void testGetExpire() {
        // 测试获取过期时间
        Duration expire = Duration.ofMinutes(10);
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .expire(expire)
                .build();

        assertEquals(expire, config.getExpire());
    }

    @Test
    void testGetLocalExpire() {
        // 测试获取本地过期时间
        Duration localExpire = Duration.ofSeconds(30);
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localExpire(localExpire)
                .build();

        assertEquals(localExpire, config.getLocalExpire());
    }

    @Test
    void testGetLocalExpireType() {
        // 测试获取本地过期类型
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localExpireType(ExpireType.AFTER_ACCESS)
                .build();

        assertEquals(ExpireType.AFTER_ACCESS, config.getLocalExpireType());
    }

    @Test
    void testGetLocalLimit() {
        // 测试获取本地限制
        Integer localLimit = 5000;
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(localLimit)
                .build();

        assertEquals(localLimit, config.getLocalLimit());
    }

    @Test
    void testGetCacheType() {
        // 测试获取缓存类型
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .cacheType(CacheType.REMOTE)
                .build();

        assertEquals(CacheType.REMOTE, config.getCacheType());
    }

    @Test
    void testGetSyncLocal() {
        // 测试获取是否同步本地
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .syncLocal(true)
                .build();

        assertTrue(config.getSyncLocal());
    }

    @Test
    void testGetValueEncoder() {
        // 测试获取值编码器
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache").build();

        assertNotNull(config.getValueEncoder());
    }

    @Test
    void testGetValueDecoder() {
        // 测试获取值解码器
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache").build();

        assertNotNull(config.getValueDecoder());
    }

    @Test
    void testGetCacheNullValue() {
        // 测试获取空值对象
        String nullValue = "NULL";
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .cacheNullValue(nullValue)
                .build();

        assertEquals(nullValue, config.getCacheNullValue());
    }

    @Test
    void testGetNullValueExpire() {
        // 测试获取空值过期时间
        Duration nullValueExpire = Duration.ofSeconds(60);
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .nullValueExpire(nullValueExpire)
                .build();

        assertEquals(nullValueExpire, config.getNullValueExpire());
    }

    @Test
    void testGetLoadLock() {
        // 测试获取加载锁配置
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .loadLock(16)
                .build();

        assertTrue(config.getLoadLock());
    }

    @Test
    void testGetLoadLockShards() {
        // 测试获取加载锁分片数
        Integer shards = 32;
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .loadLock(shards)
                .build();

        assertEquals(shards, config.getLoadLockShards());
    }

    @Test
    void testGetLoader() {
        // 测试获取缓存加载器
        CacheLoader<String, String> loader = key -> "value:" + key;
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .loader(loader)
                .build();

        assertEquals(loader, config.getLoader());
    }

    @Test
    void testGetLocalCacheType() {
        // 测试获取本地缓存类型
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache").build();

        assertEquals(LocalCacheType.CAFFEINE, config.getLocalCacheType());
    }

    @Test
    void testGetOffHeap() {
        // 测试获取是否使用堆外内存
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .offHeap(true)
                .build();

        assertTrue(config.getOffHeap());
    }

    @Test
    void testSetOffHeap() {
        // 测试设置是否使用堆外内存
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache").build();

        assertFalse(config.getOffHeap());

        config.setOffHeap(true);

        assertTrue(config.getOffHeap());
    }

    @Test
    void testGetBufReleaseDelay() {
        // 测试获取缓冲区释放延迟
        Long delay = 3000L;
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .bufReleaseDelay(delay)
                .build();

        assertEquals(delay, config.getBufReleaseDelay());
    }

    @Test
    void testSetBufReleaseDelay() {
        // 测试设置缓冲区释放延迟
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache").build();

        assertEquals(5000L, config.getBufReleaseDelay());

        config.setBufReleaseDelay(2000L);

        assertEquals(2000L, config.getBufReleaseDelay());
    }

    @Test
    void testSetLocalExpireType() {
        // 测试设置本地过期类型
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache").build();

        assertEquals(ExpireType.AFTER_WRITE, config.getLocalExpireType());

        config.setLocalExpireType(ExpireType.AFTER_ACCESS);

        assertEquals(ExpireType.AFTER_ACCESS, config.getLocalExpireType());
    }

    @Test
    void testSetLoadLockShards() {
        // 测试设置加载锁分片数
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache").build();

        config.setLoadLockShards(64);

        assertEquals(64, config.getLoadLockShards());
    }
}
