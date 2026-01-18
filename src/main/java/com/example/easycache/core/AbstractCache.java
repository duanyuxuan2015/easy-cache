package com.example.easycache.core;

import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;

/**
 * 抽象缓存类
 * <p>实现了Cache接口，定义了缓存的基本操作流程，子类只需实现具体的存储逻辑</p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {
    /** 值编码器，将对象转换为字节数组 */
    protected final Function<Object, byte[]> valueEncoder;
    /** 值解码器，将字节数组转换为对象 */
    protected final Function<byte[], Object> valueDecoder;

    /** 缓存配置 */
    protected final CacheConfig<K,V> config;

    /**
     * 构造函数
     *
     * @param config 缓存配置
     */
    public AbstractCache(CacheConfig<K,V> config) {
        this.config = config;
        valueDecoder = config.valueDecoder;
        valueEncoder = config.valueEncoder;;
    }

    /**
     * 构建缓存键
     * <p>使用配置的键转换器对原始键进行转换</p>
     *
     * @param key 原始键
     * @return 转换后的键
     */
    public String buildKey(K key) {
        String newKey = null;
        KeyConvertor<K> keyConvertor = config.getKeyConvertor();
        if (keyConvertor != null) {
            newKey = keyConvertor.apply(config.name,key);
        }
        return newKey;
    }


    @Override
    public V get(K key)  throws CacheInvokeException {
        String newKey = buildKey(key);
        return do_GET(key,newKey);
    }

    /**
     * 执行获取操作
     *
     * @param key 原始键
     * @param newKey 转换后的键
     * @return 缓存值
     * @throws CacheInvokeException 缓存调用异常
     */
    protected abstract V do_GET(K key,String newKey) throws CacheInvokeException;

    @Override
    public  Map<K, V> getAll(Set<K> keys) throws CacheInvokeException {
        Map<String,K> keyMap = new HashMap<>(keys.size());
        keys.forEach(key -> keyMap.put(buildKey(key),key));
        return do_GET_ALL(keyMap);
    }

    /**
     * 执行批量获取操作
     *
     * @param keys 转换后的键与原始键的映射
     * @return 键值对映射
     */
    protected abstract Map<K, V> do_GET_ALL(Map<String,K>  keys);


    @Override
    public final void put(K key, V value) {
        String newKey = buildKey(key);
        do_PUT(newKey,value);
    }

    /**
     * 执行添加操作
     *
     * @param stringKey 转换后的键
     * @param value 值
     */
    protected abstract void do_PUT(String stringKey, V value);

    @Override
    public final void putAll(Map<K, V> map) {
        Map<String,V> newMap = new HashMap<>();
        map.entrySet().stream()
                .filter(entry -> entry.getValue() != null || config.cacheNullValue != null)
                .forEach(entry -> newMap.put(buildKey(entry.getKey()), entry.getValue()));
        if(CollectionUtils.isEmpty(newMap)) return;
        do_PUT_ALL(newMap);
    }

    /**
     * 执行批量添加操作
     *
     * @param map 键值对映射
     */
    protected abstract void do_PUT_ALL(Map<String,V> map);

    @Override
    public final boolean remove(K key) {
        String newKey = buildKey(key);
        return do_REMOVE(newKey);
    }

    /**
     * 执行移除操作
     *
     * @param key 转换后的键
     * @return 是否移除成功
     */
    protected abstract boolean do_REMOVE(String key);

    @Override
    public final void removeAll(Set<K> keys) {
       Set<String> keySet = new HashSet<>(keys.size());
       keys.forEach(key -> keySet.add(buildKey(key)));
       do_REMOVE_ALL(keySet);
    }

    /**
     * 执行批量移除操作
     *
     * @param keys 转换后的键集合
     */
    protected abstract void do_REMOVE_ALL(Set<String> keys);

    @Override
    public final CacheConfig<K,V> config(){
        return config;
    }

}
