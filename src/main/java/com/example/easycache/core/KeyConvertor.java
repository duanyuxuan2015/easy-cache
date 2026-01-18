package com.example.easycache.core;

/**
 * 键转换器接口
 * <p>用于将原始键转换为缓存键</p>
 *
 * @param <K> 原始键类型
 */
public interface KeyConvertor<K> {
    /**
     * 转换键
     *
     * @param cacheName 缓存名称
     * @param key 原始键
     * @return 转换后的键
     */
    String apply(String cacheName , K key);
}
