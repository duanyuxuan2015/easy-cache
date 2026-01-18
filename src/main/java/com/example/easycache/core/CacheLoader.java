package com.example.easycache.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 缓存加载器接口
 * <p>用于在缓存未命中时从数据源加载数据</p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public interface CacheLoader<K, V>  {
    /**
     * 加载单个值
     *
     * @param key 键
     * @return 加载的值
     * @throws Throwable 加载异常
     */
    V load(K key) throws Throwable;

    /**
     * 批量加载值
     *
     * @param keys 键集合
     * @return 键值对映射
     * @throws Throwable 加载异常
     */
    default Map<K, V> loadAll(Set<K> keys) throws Throwable {
        Map<K, V> map = new HashMap<>();
        for (K k : keys) {
            map.put(k, load(k));
        }
        return map;
    }


}
