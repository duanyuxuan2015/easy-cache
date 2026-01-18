package com.example.easycache.core;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis缓存实现
 * <p>使用Redis作为远程缓存存储</p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class RedisCache<K,V> extends AbstractCache<K,V> {

    /** Redis模板 */
    private final RedisTemplate<String,byte[]> redisTemplate;

    /**
     * 构造函数
     *
     * @param config 缓存配置
     * @param redisTemplate Redis模板
     */
    public RedisCache(CacheConfig<K,V> config, RedisTemplate<String,byte[]> redisTemplate) {
        super(config);
        this.redisTemplate = redisTemplate;
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
        byte[] bytes = redisTemplate.opsForValue().get(newKey);
        if(bytes == null) return null;
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
        Map<K,V> resultMap = new HashMap<>();
        List<byte[]> bytesList = redisTemplate.opsForValue().multiGet(keys.keySet());
        int i =0 ;
        for(String key : keys.keySet()) {
            byte[] bytes = bytesList.get(i++);
            if (bytes == null) {
                resultMap.put(keys.get(key), null);
            } else {
                resultMap.put(keys.get(key), (V) valueDecoder.apply(bytes));
            }
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
        byte[] bytes = valueEncoder.apply(value);
        if(config().expire!=null) {
            redisTemplate.opsForValue().set(key,bytes,config().expire);
        }else {
            redisTemplate.opsForValue().set(key,bytes);
        }

    }

    /**
     * 批量添加缓存
     *
     * @param map 键值对映射
     */
    @Override
    protected void do_PUT_ALL(Map<String, V> map) {
        Map<String, byte[]> newMap = new HashMap<>(map.size());
        map.forEach((key, value) -> {
            if(value !=null) {
                newMap.put(key, valueEncoder.apply(value));
            }
        });
        if(newMap.isEmpty()) return;
        if (config().expire == null) {
            redisTemplate.opsForValue().multiSet(newMap);
        }else {
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    RedisOperations<String, byte[]> valueOps = (RedisOperations<String, byte[]>) operations;
                    newMap.forEach((key, value) -> valueOps.opsForValue().set(key,value, config().expire));
                    return null;
                }
            });
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
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 批量移除缓存
     *
     * @param keys 键集合
     */
    @Override
    protected void do_REMOVE_ALL(Set<String> keys) {
        redisTemplate.delete(keys);
    }
}
