package com.example.easycache;

import com.alibaba.fastjson2.JSON;
import com.example.easycache.core.*;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Sets;
import org.caffinitas.ohc.Eviction;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 主类
 * <p>用于测试缓存功能</p>
 */
public class Main {
    public static void main(String [] args) throws InterruptedException {
        CacheConfig<String,String> config =  new CacheConfigBuilder<String,String>("myCache").localLimit(100).localExpire(Duration.ofMillis(100)).keyConvertor((cacheName, key) -> key).build();
        OHCCache<String,String> ohcCache = new OHCCache<>(config);
        ohcCache.put("11","22");
        ohcCache.put("12","22");
        //System.out.println(ohcCache.get("11"));

        //System.out.println(JSON.toJSONString(ohcCache.getAll(Sets.newHashSet("11","12"))));

        Thread.sleep(200);

        //System.out.println(ohcCache.get("11"));

        OHCacheBuilder<String,byte[]> builder = OHCacheBuilder.<String, byte[]>newBuilder();
        builder.keySerializer(new OhcCacheKeySerializer())
                .valueSerializer(new OhcCacheValueSerializer())
                .eviction(Eviction.LRU)
                .capacity(config.getLocalLimit() == null ? CacheConstants.DEFAULT_LOCAL_LIMIT * 1024 * 1024 : config.getLocalLimit() * 1024 * 1024);
        builder.timeouts(true);
        OHCache<String, byte[]> ohCache = builder.build();
        ohCache.put("1","2".getBytes(StandardCharsets.UTF_8),System.currentTimeMillis()+Duration.ofMillis(100).toMillis());
        Thread.sleep(50);
        System.out.println(new String(ohCache.get("1")));
        Thread.sleep(50);
        System.out.println(ohCache.get("1") == null ? "null": new String(ohCache.get("1")));

        Cache<String,String> stringCaffeine = Caffeine.newBuilder()
                .maximumSize(10_000) // 最大缓存条目数（超过则按策略淘汰）
                .expireAfterWrite(5, TimeUnit.MINUTES) // 写入后5分钟过期
                .build();

    }
}
