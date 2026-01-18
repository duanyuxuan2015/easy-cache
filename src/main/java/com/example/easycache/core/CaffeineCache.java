package com.example.easycache.core;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.jspecify.annotations.NonNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Caffeine本地缓存实现
 * <p>基于Caffeine的高性能本地缓存</p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class CaffeineCache<K, V> extends AbstractCache<K, V> {

    /** Caffeine缓存实例 */
    com.github.benmanes.caffeine.cache.Cache<Object, Object> caffineCache;

    /**
     * 构造函数
     *
     * @param config 缓存配置
     */
    public CaffeineCache(CacheConfig<K, V> config) {
        super(config);
        Caffeine<Object,Object> caffeine= Caffeine.newBuilder()
                .maximumSize(config.getLocalLimit() == null ? CacheConstants.DEFAULT_LOCAL_LIMIT : config.getLocalLimit());
        long cacheTTL = config.localExpire != null ?config.localExpire.toNanos():Long.MAX_VALUE;
        Expiry<Object, Object> customExpiry = new Expiry<>() {
            @Override
            public long expireAfterCreate(Object key, Object value, long currentTime) {
                if(config.nullValueExpire != null){
                    if(value.equals(config.cacheNullValue)){
                        return config.nullValueExpire.toNanos();
                    }
                }
                return cacheTTL;
            }
            @Override
            public long expireAfterUpdate(Object key, Object value, long currentTime, long currentDuration) {

                if(config.localExpireType.equals(ExpireType.AFTER_WRITE)){
                    return currentDuration;
                }

                if(config.nullValueExpire != null){
                    if(value.equals(config.cacheNullValue)){
                        return config.nullValueExpire.toNanos();
                    }
                }
                return cacheTTL;
            }
            @Override
            public long expireAfterRead(Object key, Object value, long currentTime, long currentDuration) {
                if(config.localExpireType.equals(ExpireType.AFTER_WRITE)){
                    return currentDuration;
                }
                if(config.nullValueExpire != null){
                    if(value.equals(config.cacheNullValue)){
                        return currentDuration;
                    }
                }
                return cacheTTL;
            }
        };
        caffeine.expireAfter(customExpiry);
        caffineCache = caffeine.build();
    }


    /**
     * 获取缓存值
     *
     * @param key 原始键
     * @param newKey 转换后的键
     * @return 缓存值
     */
    @Override
    protected V do_GET(K key,String newKey) {
        return (V) caffineCache.getIfPresent(newKey);
    }

    /**
     * 批量获取缓存值
     *
     * @param keys 转换后的键与原始键的映射
     * @return 键值对映射
     */
    @Override
    protected Map<K, V> do_GET_ALL(Map<String, K> keys) {
        Map<K, V> resultMap = new HashMap<>();
        Map<Object, @NonNull Object> cacheResultMap = caffineCache.getAllPresent(keys.keySet());
        // 为所有请求的键添加结果，不存在的键返回null
        for (Map.Entry<String, K> entry : keys.entrySet()) {
            K originalKey = entry.getValue();
            Object value = cacheResultMap.get(entry.getKey());
            resultMap.put(originalKey, (V) value);
        }
        return resultMap;
    }

    /**
     * 添加缓存
     *
     * @param key 键
     * @param value 值
     */
    @Override
    protected void do_PUT(String key, V value) {
        // Caffeine不支持null值，如果配置了cacheNullValue，则存储该值
        if (value == null) {
            if (config().cacheNullValue != null) {
                caffineCache.put(key, config().cacheNullValue);
            }
            // 如果没有配置cacheNullValue，则不存储null值
        } else {
            caffineCache.put(key, value);
        }
    }

    /**
     * 批量添加缓存
     *
     * @param map 键值对映射
     */
    @Override
    protected void do_PUT_ALL(Map<String, V> map) {
        Map<String,V> notNullMap = new HashMap<>(map.size());
        Map<String,V> nullValueMap = new HashMap<>();
        map.forEach((key, value) -> {
            if(config().cacheNullValue == null || value!= null){
                notNullMap.put(key,value);
            } else{
                nullValueMap.put(key,config().cacheNullValue);
            }
        });
        if(!notNullMap.isEmpty())caffineCache.putAll(notNullMap);
        if(nullValueMap.isEmpty()) return;
        caffineCache.putAll(nullValueMap);
    }

    /**
     * 移除缓存
     *
     * @param key 键
     * @return 是否移除成功
     */
    @Override
    protected boolean do_REMOVE(String key) {
        boolean keyExist = caffineCache.getIfPresent(key) !=null;
        caffineCache.invalidate(key);
        return keyExist;
    }

    /**
     * 批量移除缓存
     *
     * @param keys 键集合
     */
    @Override
    protected void do_REMOVE_ALL(Set<String> keys) {
        caffineCache.invalidateAll(keys);
    }


}
