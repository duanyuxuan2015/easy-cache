package com.example.easycache.core;


import java.util.Map;
import java.util.Set;

/**
 * 缓存接口
 * <p>定义了缓存的基本操作，包括获取、添加、删除等</p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public interface Cache<K, V> {

    /**
     * 根据键获取缓存值
     *
     * @param key 键
     * @return 缓存值，如果不存在则返回null
     * @throws CacheInvokeException 缓存调用异常
     */
    V get(K key) throws CacheInvokeException ;

    /**
     * 批量获取缓存值
     *
     * @param keys 键集合
     * @return 键值对映射
     * @throws CacheInvokeException 缓存调用异常
     */
    Map<K, V> getAll(Set<K> keys) throws CacheInvokeException;

    /**
     * 添加缓存
     *
     * @param key 键
     * @param value 值
     */
    void put(K key, V value);

    /**
     * 批量添加缓存
     *
     * @param map 键值对映射
     */
    void putAll(Map<K,V> map);

    /**
     * 移除缓存
     *
     * @param key 键
     * @return 是否移除成功
     */
    boolean remove(K key);

    /**
     * 批量移除缓存
     *
     * @param keys 键集合
     */
    void removeAll(Set<K> keys);

    /**
     * 获取缓存配置
     *
     * @return 缓存配置对象
     */
    CacheConfig<K,V> config();

}
