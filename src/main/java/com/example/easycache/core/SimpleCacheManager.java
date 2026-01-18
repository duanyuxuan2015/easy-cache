package com.example.easycache.core;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单缓存管理器实现
 * <p>负责创建和管理多个缓存实例</p>
 */
public class SimpleCacheManager implements CacheManager {
    private static final Logger logger = LoggerFactory.getLogger(SimpleCacheManager.class);

    /** 缓存映射 */
    private final ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    /** 广播管理器 */
    private BroadcastManager broadcastManager;

    /**
     * 构造函数
     *
     * @param redisTemplate Redis模板
     */
    public SimpleCacheManager(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /** Redis模板 */
    private final RedisTemplate<String, byte[]> redisTemplate;

    /**
     * 根据名称获取缓存
     *
     * @param cacheName 缓存名称
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 缓存实例
     */
    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return cacheMap.get(cacheName);
    }

    /**
     * 根据配置获取或创建缓存
     *
     * @param config 缓存配置
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 缓存实例
     */
    @Override
    public <K, V> Cache<K, V> getOrCreateCache(CacheConfig<K, V> config) {
        return cacheMap.computeIfAbsent(config.getName(), n -> createCache(config));

    }

    /**
     * 批量获取多个缓存的值
     *
     * @param requests 批量请求列表
     * @return 批量响应列表
     */
    @Override
    public List<BatchResponse> multiGet(List<BatchRequest> requests) {

        Map<String, Map> resultMap = new HashMap<>();
        Map<String, Set> noResultKeysMap = new HashMap<>();
        List<BatchResponse> batchResponseList = new ArrayList<>();
        for (BatchRequest request : requests) {
            MultiLevelCache cache = (MultiLevelCache) getCache(request.getCacheName());
            if (cache.localCache != null) {
                Map localResult = cache.localCache.getAll(request.getKeys());
                logger.info(String.format("load from local cache ： %s ,result is ： %s",request.getCacheName(),JSON.toJSONString(localResult)));
                resultMap.put(request.getCacheName(), localResult);
                Set noResultKeys = calcNoResultKeys(request.getKeys(), localResult);
                if (noResultKeys.size() > 0) noResultKeysMap.put(request.getCacheName(), noResultKeys);
            }
        }

        List<Object> resultList = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                RedisOperations<String, byte[]> valueOps = (RedisOperations<String, byte[]>) operations;
                for (Map.Entry<String, Set> entry : noResultKeysMap.entrySet()) {
                    String cacheName = entry.getKey();
                    Set keys = entry.getValue();
                    MultiLevelCache cache = (MultiLevelCache) getCache(cacheName);
                    Set<String> newKeys = new HashSet<>();
                    for (Object key : keys) {
                        newKeys.add(cache.buildKey(key));
                    }
                    valueOps.opsForValue().multiGet(newKeys);
                }
                return null;
            }
        });

        int iter =0;
        for (Map.Entry<String, Set> entry : noResultKeysMap.entrySet()){
            if(entry.getValue().isEmpty()) continue;
            String cacheName = entry.getKey();
            MultiLevelCache cache = (MultiLevelCache) getCache(cacheName);
            if(cache.remoteCache== null) continue;
            Set keys = entry.getValue();
            Set hasResultKeys = new HashSet();
            Map cacheResult = resultMap.computeIfAbsent(cacheName,n -> new HashMap<>());
            List remoteResultList = new ArrayList();
            List<byte[]> bytesList = (List<byte[]>) resultList.get(iter++);
            int i =0 ;
            for(Object key : keys) {
                byte[] bytes = bytesList.get(i++);
                if (bytes != null) {
                    hasResultKeys.add(key);
                    Object value = cache.config().getValueDecoder().apply(bytes);
                    remoteResultList.add(value);
                    cacheResult.put(key,value);
                    if(cache.localCache!=null) cache.localCache.put(key,value);
                }
            }
            logger.info(String.format("load from remote cache ： %s ,keys: %s ,result is ： %s",cacheName,JSON.toJSONString(keys),JSON.toJSONString(remoteResultList)));
            keys.removeAll(hasResultKeys);
        }

        for (Map.Entry<String, Set> entry : noResultKeysMap.entrySet()){
            if(entry.getValue().isEmpty()) continue;
            String cacheName = entry.getKey();
            Set keys = entry.getValue();
            MultiLevelCache cache = (MultiLevelCache) getCache(cacheName);
            Map cacheResult = resultMap.computeIfAbsent(cacheName,n -> new HashMap<>());
            Set<String> stringKeys = new HashSet<>();
            for (Object key : keys) {
                stringKeys.add(cache.buildKey(key));
            }
            Map loadResult ;
            try {
                loadResult = cache.loadAll(stringKeys,keys);
            } catch (Throwable e) {
                logger.error("failed to load cache,{}", JSON.toJSONString(keys),e);
                throw new CacheInvokeException(e);
            }
            Set hasResultKeySet = new HashSet();
            loadResult.entrySet().forEach(resultEntry -> {
                Map.Entry loadResultEntry = (Map.Entry) resultEntry;
                if(loadResultEntry.getValue()!=null){
                    hasResultKeySet.add(loadResultEntry.getKey());
                    cache.put(loadResultEntry.getKey(),loadResultEntry.getValue());
                }
            });
            logger.info(String.format("load from loader ： %s ,keys : %s ,result is ： %s",cacheName,JSON.toJSONString(keys),JSON.toJSONString(loadResult.values())));


            keys.removeAll(hasResultKeySet);
            cacheResult.putAll(loadResult);
            if(cache.config.cacheNullValue!=null && cache.localCache!=null) {
                for (Object key : keys) {
                    cache.localCache.put(key, cache.config.cacheNullValue);
                }
            }
        }

        for (Map.Entry<String, Map> entry : resultMap.entrySet()){
            String cacheName = entry.getKey();
            BatchResponse batchResponse = new BatchResponse();
            batchResponse.setCacheName(cacheName);
            batchResponse.setValues(entry.getValue().values());
            batchResponseList.add(batchResponse);
        }
        return batchResponseList;
    }

    /**
     * 计算未命中的键
     *
     * @param keys 所有键
     * @param localResult 本地缓存结果
     * @return 未命中的键集合
     */
    private Set calcNoResultKeys(Set keys, Map localResult) {
        Set noResultKeys = new HashSet();
        for (Object key : keys) {
            if (localResult.get(key) == null) {
                noResultKeys.add(key);
            }
        }
        return noResultKeys;
    }

    /**
     * 创建多级缓存
     *
     * @param config 缓存配置
     * @param <K> 键类型
     * @param <V> 值类型
     * @return 多级缓存实例
     */
    private <K, V> MultiLevelCache<K, V> createCache(CacheConfig<K, V> config) {
        return new MultiLevelCache<>(config, redisTemplate, broadcastManager);
    }

    /**
     * 设置广播管理器
     *
     * @param broadcastManager 广播管理器
     */
    @Override
    public void setBroadcastManager(BroadcastManager broadcastManager) {
        this.broadcastManager = broadcastManager;
    }
}
