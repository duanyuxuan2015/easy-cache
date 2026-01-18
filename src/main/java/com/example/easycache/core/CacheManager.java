package com.example.easycache.core;

import java.util.List;

/**
 * 缓存管理器接口
 * <p>负责缓存的创建、获取和管理</p>
 */
public interface CacheManager {
    /**
     * 根据缓存名称获取缓存实例
     *
     * @param cacheName 缓存名称
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 缓存实例
     */
    <K, V> Cache<K, V> getCache(String cacheName);

    /**
     * 根据配置获取或创建缓存实例
     *
     * @param config 缓存配置
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 缓存实例
     */
    <K, V> Cache<K, V> getOrCreateCache(CacheConfig<K,V> config);

    /**
     * 批量获取多个缓存的值
     *
     * @param requests 批量请求集合
     * @return 批量响应集合
     */
    List<BatchResponse> multiGet(List<BatchRequest> requests);

    /**
     * 设置广播管理器，用于集群间的缓存同步
     *
     * @param broadcastManager 广播管理器
     */
    void setBroadcastManager(BroadcastManager broadcastManager);
}
