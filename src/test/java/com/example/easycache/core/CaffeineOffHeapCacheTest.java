package com.example.easycache.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

import com.example.easycache.core.Fastjson2KeyConvertor;

/**
 * CaffeineOffHeapCache Caffeine堆外缓存单元测试
 */
public class CaffeineOffHeapCacheTest {

    @BeforeEach
    void setUp() {
        // 初始化默认执行器
        EasyCacheExecutor.defaultExecutor();
    }

    @Test
    void testCreateCache() {
        // 测试创建缓存
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .localExpire(java.time.Duration.ofMinutes(10))
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineOffHeapCache<String, String> cache = new CaffeineOffHeapCache<>(config);

        assertNotNull(cache);
        assertEquals("testCache", cache.config().getName());
    }

    @Test
    void testPutAndGet() {
        // 测试基本的put和get操作
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .localExpire(java.time.Duration.ofMinutes(10))
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineOffHeapCache<String, String> cache = new CaffeineOffHeapCache<>(config);

        cache.put("key1", "value1");
        String value = cache.get("key1");

        assertEquals("value1", value);
    }

    @Test
    void testGetNonExistentKey() {
        // 测试获取不存在的键
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineOffHeapCache<String, String> cache = new CaffeineOffHeapCache<>(config);

        String value = cache.get("nonExistent");

        assertNull(value);
    }

    @Test
    void testRemove() {
        // 测试移除键
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineOffHeapCache<String, String> cache = new CaffeineOffHeapCache<>(config);

        cache.put("key1", "value1");
        boolean removed = cache.remove("key1");

        assertTrue(removed);
        assertNull(cache.get("key1"));
    }

    @Test
    void testPutAll() {
        // 测试批量存储
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineOffHeapCache<String, String> cache = new CaffeineOffHeapCache<>(config);

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
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineOffHeapCache<String, String> cache = new CaffeineOffHeapCache<>(config);

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
    void testRemoveAll() {
        // 测试批量移除
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineOffHeapCache<String, String> cache = new CaffeineOffHeapCache<>(config);

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
    void testDelayedByteBufTask() throws InterruptedException {
        // 测试延迟ByteBuf任务
        ByteBuf buf = NettyMemoryPool.allocateDirectBuffer(1024);

        CaffeineOffHeapCache.DelayedByteBufTask task =
            new CaffeineOffHeapCache.DelayedByteBufTask(buf, 100, TimeUnit.MILLISECONDS);

        assertFalse(task.getDelay(TimeUnit.MILLISECONDS) <= 0);

        Thread.sleep(150);

        assertTrue(task.getDelay(TimeUnit.MILLISECONDS) <= 0);
    }

    @Test
    void testDelayedByteBufTaskRelease() {
        // 测试延迟ByteBuf任务释放
        ByteBuf buf = NettyMemoryPool.allocateDirectBuffer(1024);

        CaffeineOffHeapCache.DelayedByteBufTask task =
            new CaffeineOffHeapCache.DelayedByteBufTask(buf, 0, TimeUnit.MILLISECONDS);

        assertEquals(1, buf.refCnt());

        task.release();

        assertEquals(0, buf.refCnt());
    }

    @Test
    void testDelayedByteBufTaskCompareTo() {
        // 测试延迟ByteBuf任务比较
        ByteBuf buf1 = NettyMemoryPool.allocateDirectBuffer(1024);
        ByteBuf buf2 = NettyMemoryPool.allocateDirectBuffer(1024);

        CaffeineOffHeapCache.DelayedByteBufTask task1 =
            new CaffeineOffHeapCache.DelayedByteBufTask(buf1, 100, TimeUnit.MILLISECONDS);
        CaffeineOffHeapCache.DelayedByteBufTask task2 =
            new CaffeineOffHeapCache.DelayedByteBufTask(buf2, 200, TimeUnit.MILLISECONDS);

        assertTrue(task1.compareTo(task2) < 0);
        assertTrue(task2.compareTo(task1) > 0);
        assertEquals(0, task1.compareTo(task1));

        NettyMemoryPool.releaseBuffer(buf1);
        NettyMemoryPool.releaseBuffer(buf2);
    }

    @Test
    void testConfig() {
        // 测试获取配置
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineOffHeapCache<String, String> cache = new CaffeineOffHeapCache<>(config);

        CacheConfig<String, String> retrievedConfig = cache.config();

        assertNotNull(retrievedConfig);
        assertEquals("testCache", retrievedConfig.getName());
    }

    @Test
    void testWithOffHeapEnabled() {
        // 测试启用堆外内存
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .offHeap(true)
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineOffHeapCache<String, String> cache = new CaffeineOffHeapCache<>(config);

        assertTrue(cache.config().getOffHeap());
    }

    @Test
    void testPutAllWithNullValues() {
        // 测试批量存储包含null值
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(100)
                .cacheNullValue("NULL")
                .nullValueExpire(java.time.Duration.ofMinutes(5))
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineOffHeapCache<String, String> cache = new CaffeineOffHeapCache<>(config);

        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", null);
        map.put("key3", "value3");

        cache.putAll(map);

        assertEquals("value1", cache.get("key1"));
        assertEquals("NULL", cache.get("key2"));
        assertEquals("value3", cache.get("key3"));
    }

    @Test
    void testLargeData() {
        // 测试存储大量数据
        CacheConfig<String, String> config = new CacheConfigBuilder<String, String>("testCache")
                .localLimit(200)
                .keyConvertor(Fastjson2KeyConvertor.INSTANCE)
                .build();

        CaffeineOffHeapCache<String, String> cache = new CaffeineOffHeapCache<>(config);

        for (int i = 0; i < 100; i++) {
            cache.put("key" + i, "value" + i);
        }

        for (int i = 0; i < 100; i++) {
            assertEquals("value" + i, cache.get("key" + i));
        }
    }
}
