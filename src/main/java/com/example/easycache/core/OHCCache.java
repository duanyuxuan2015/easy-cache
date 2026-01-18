package com.example.easycache.core;

import org.caffinitas.ohc.Eviction;
import org.caffinitas.ohc.OHCache;
import org.caffinitas.ohc.OHCacheBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * OHC（Off-Heap Cache）堆外缓存实现
 * <p>使用堆外内存作为本地缓存，减少GC压力</p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class OHCCache<K, V> extends AbstractCache<K, V> {

    /** OHC缓存实例 */
    OHCache<String, byte[]> ohCache;

    /**
     * 构造函数
     *
     * @param config 缓存配置
     */
    public OHCCache(CacheConfig<K, V> config) {
        super(config);
        OHCacheBuilder<String,byte[]> builder = OHCacheBuilder.<String, byte[]>newBuilder();
        builder.keySerializer(new OhcCacheKeySerializer())
                .valueSerializer(new OhcCacheValueSerializer())
                .eviction(Eviction.LRU)
                .capacity(config.getLocalLimit() == null ? CacheConstants.DEFAULT_LOCAL_LIMIT * 1024 * 1024 : config.getLocalLimit() * 1024 * 1024);

        if (config.localExpire != null) {
            builder.timeouts(true).defaultTTLmillis(config.localExpire.toMillis());
        }else if(config.nullValueExpire != null){
            builder.timeouts(true);
        }
        ohCache = builder.build();
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
        byte[] bytes = ohCache.get(newKey);
        if (bytes == null) return null;
        return (V) valueDecoder.apply(bytes);
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
        // 为所有请求的键添加结果，不存在的键返回null
        keys.forEach((key, originalKey) -> {
            byte[] bytes = ohCache.get(key);
            if (bytes != null) {
                resultMap.put(originalKey, (V) valueDecoder.apply(bytes));
            } else {
                resultMap.put(originalKey, null);
            }
        });
        return resultMap;
    }

    /**
     * 添加缓存
     *
     * @param key 键
     * @param value 值
     */
    @Override
    protected void do_PUT(String key,  V value) {
        byte[] bytes = valueEncoder.apply(value);
        if(config().cacheNullValue == null){
            ohCache.put(key, bytes);
            return;
        }
        if(value == config().cacheNullValue && config().nullValueExpire !=null){
            ohCache.put(key,bytes,System.currentTimeMillis()+config().nullValueExpire.toMillis());
        }else{
            ohCache.put(key, bytes);
        }
    }

    /**
     * 批量添加缓存
     *
     * @param map 键值对映射
     */
    @Override
    protected void do_PUT_ALL(Map<String, V> map) {
        Map<String, byte[]> serializeMap = new HashMap<>(map.size());
        Map<String, byte[]> nullValueMap = new HashMap<>();
        map.forEach((key, value) -> {
            if(config().cacheNullValue == null || value!= null){
                serializeMap.put(key,valueEncoder.apply(value));
            } else{
                nullValueMap.put(key,valueEncoder.apply(config().cacheNullValue));
            }
        });
        if(!serializeMap.isEmpty())ohCache.putAll(serializeMap);
        if(nullValueMap.isEmpty()) return;
        if(config().nullValueExpire!=null) {
            long expireTimeStamp = System.currentTimeMillis() + config().nullValueExpire.toMillis();
            nullValueMap.forEach((key, value) -> ohCache.put(key, value, expireTimeStamp));
        }else{
            nullValueMap.forEach((key, value) -> ohCache.put(key, value));
        }
    }

    /**
     * 移除缓存
     *
     * @param key 键
     * @return 是否移除成功
     */
    @Override
    protected boolean do_REMOVE(String key) {
        return ohCache.remove(key);
    }

    /**
     * 批量移除缓存
     *
     * @param keys 键集合
     */
    @Override
    protected void do_REMOVE_ALL(Set<String> keys) {
        ohCache.removeAll(keys);
    }


}
