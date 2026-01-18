package com.example.easycache.core;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多级缓存实现
 * <p>支持本地缓存和远程缓存的组合，提供缓存穿透保护、自动加载等功能</p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class MultiLevelCache<K,V> extends AbstractCache<K,V> {

    private static final Logger logger = LoggerFactory.getLogger(MultiLevelCache.class);

    /** 本地缓存实例 */
    protected AbstractCache<K,V> localCache;

    /** 远程缓存实例 */
    protected AbstractCache<K,V> remoteCache = null;

    /** 加载锁映射，用于防止缓存击穿 */
    private HashMap<Integer, ReentrantLock> loadLockMap;

    /** 广播管理器，用于集群间缓存同步 */
    private final BroadcastManager broadcastManager;

    /**
     * 构造函数
     *
     * @param config 缓存配置
     * @param redisTemplate Redis模板
     * @param broadcastManager 广播管理器
     */
    public MultiLevelCache(CacheConfig<K,V> config,RedisTemplate<String,byte[]> redisTemplate,BroadcastManager broadcastManager) {
        super(config);
        if(hasLocalCache()) {
            if(config.localCacheType.equals(LocalCacheType.OHC)){
                localCache = new OHCCache<>(config);
            }else if(config.offHeap){
                localCache = new CaffeineOffHeapCache<>(config);
            }else {
                localCache = new CaffeineCache<>(config);
            }
        }
        if(!onlyLocalCache()) remoteCache = new RedisCache<>(config,redisTemplate);
        this.broadcastManager = broadcastManager;
        if(config.loader != null && config.loadLock){
            loadLockMap= new HashMap<>(config.loadLockShards);
            for(int i =0;i<config.loadLockShards;i++){
                loadLockMap.put(i,new ReentrantLock());
            }
        }
    }

    /**
     * 获取本地缓存实例
     *
     * @return 本地缓存实例
     */
    public AbstractCache<K, V> getLocalCache() {
        return localCache;
    }

    /**
     * 获取缓存值
     * <p>按照本地缓存 -> 远程缓存 -> 缓存加载器的顺序查找</p>
     *
     * @param key 原始键
     * @param newKey 转换后的键
     * @return 缓存值
     */
    @Override
    protected V do_GET(K key ,String newKey)   {
        V result = null;
        if(hasLocalCache()){
            result = localCache.do_GET(key,newKey);
            logger.info("get from local cache ,key:{} result: {}",newKey,JSON.toJSONString(result));
            if(result == null && remoteCache != null){
                result = remoteCache.do_GET(key,newKey);
                logger.info("get from remote cache ,no result key:{} result: {}",newKey,JSON.toJSONString(result));
                if(result!= null) localCache.do_PUT(newKey,result);
            }
        }else if (config().getCacheType().equals(CacheType.REMOTE)){
            result = remoteCache.do_GET(key,newKey);
            logger.info("get from remote cache ,key:{} result: {}",newKey,JSON.toJSONString(result));
        }
        if(result == null && config().getLoader()!= null){
            try {
                result = load(newKey,key);
            } catch (Throwable e) {
                logger.error("failed to load cache,{}",newKey,e);
                throw new CacheInvokeException(e);
            }
            logger.info("load data ,no result key:{} result: {}",newKey,JSON.toJSONString(result));
            if(result !=null) {
                if(remoteCache != null) {
                    do_PUT(newKey,result);
                }else {
                    localCache.do_PUT(newKey,result);
                }
            }else if(hasLocalCache()&&config().cacheNullValue != null){
                localCache.do_PUT(newKey, config().cacheNullValue);
            }
        }
        return result;
    }

    /**
     * 批量获取缓存值
     *
     * @param keys 转换后的键与原始键的映射
     * @return 键值对映射
     */
    @Override
    protected Map<K, V> do_GET_ALL(Map<String, K> keys) {
        Map<K,V> result = null;
        if(hasLocalCache()){
            result = localCache.do_GET_ALL(keys);
            logger.info("get from local cache ,key:{} result: {}",JSON.toJSONString(keys.keySet()),JSON.toJSONString(result));
            if(result.size()< keys.size() && remoteCache != null){
                Map<K,V> remoteResult = null;
                Map<String,K> noResultKeys  = calcNoResultKeys(result,keys);
                remoteResult = remoteCache.do_GET_ALL(noResultKeys);
                logger.info("get from remote cache ,no result key:{} result: {}",JSON.toJSONString(noResultKeys.keySet()),JSON.toJSONString(remoteResult));
                if(remoteResult!=null&&!remoteResult.isEmpty()){
                    result.putAll(remoteResult);
                    localCache.putAll(remoteResult);
                }
            }
        }else if (config().getCacheType().equals(CacheType.REMOTE)){
            result = remoteCache.do_GET_ALL(keys);
            logger.info("get from remote cache ,key:{} result: {}",JSON.toJSONString(keys.keySet()),JSON.toJSONString(result));
        }
        //计算出没有从缓存返回的key
        Map<String,K> noResultKeys  = calcNoResultKeys(result,keys);
        if(noResultKeys.isEmpty()) return result;
        logger.info("empty keys :{} ",JSON.toJSONString(noResultKeys.keySet()));
        //从loader批量加载
        if(config().getLoader()!= null){
            Map<K, V> loadResult = null;
            try {
                loadResult = loadAll(noResultKeys.keySet(),Sets.newHashSet(noResultKeys.values()));
                logger.info("load data ,key:{} result: {}",JSON.toJSONString(noResultKeys.keySet()),JSON.toJSONString(loadResult));
            } catch (Throwable e) {
                logger.error("failed to load cache,{}", JSON.toJSONString(noResultKeys.keySet()),e);
                throw new CacheInvokeException(e);
            }
            if(loadResult != null && !loadResult.isEmpty()){
                result.putAll(loadResult);
                if(remoteCache != null){
                    putAll(loadResult);
                }else{
                    Map<String,V> newMap = new HashMap<>();
                    loadResult.entrySet().stream()
                            .filter(entry -> entry.getValue() != null).forEach(entry -> newMap.put(buildKey(entry.getKey()),entry.getValue()));
                    if(!CollectionUtils.isEmpty(newMap)){
                        if(hasLocalCache()) localCache.do_PUT_ALL(newMap);
                    }
                }
            }
            //处理空值，防止缓存穿透
            if(hasLocalCache()&&config().cacheNullValue !=null){
                Map<String,K> noResultFinalKeys  = calcNoResultKeys(loadResult,noResultKeys);
                if(!noResultFinalKeys.isEmpty()) {
                    noResultFinalKeys.forEach((key,originalKey) -> localCache.do_PUT(key, config().cacheNullValue));
                    logger.info("set local cache to prevent cache penetration ,key:{}",JSON.toJSONString(noResultFinalKeys.keySet()));

                }
            }
        }
        return result;
    }



    /**
     * 计算缓存不存在或者值为null的key值
     * @param result 当前结果
     * @param keys 所有键
     * @return 未命中的键映射
     */
    private Map<String,K> calcNoResultKeys(Map<K,V> result, Map<String,K> keys){
        if(result == null || result.isEmpty()) return  keys;
        Map<String,K> noResultKeys  = new HashMap<>();
        keys.forEach((key, value) -> {
            if (result.get(value) == null || !result.containsKey(value)) {
                noResultKeys.put(key, value);
            }
        });
        return noResultKeys;
    }

    /**
     * 计算loader没有返回的key值
     * @param result 加载结果
     * @param keys 所有键
     * @return 未返回的键映射
     */
    private Map<String,K> calcNoReturnKeys(Map<K,V> result, Map<String,K> keys){
        if(result == null || result.isEmpty()) return  keys;
        Map<String,K> noResultKeys  = new HashMap<>();
        keys.forEach((key, value) -> {
            if (!result.containsKey(value)) {
                noResultKeys.put(key, value);
            }
        });
        return noResultKeys;
    }

    /**
     * 添加缓存
     * <p>同时更新本地缓存和远程缓存</p>
     *
     * @param key 键
     * @param value 值
     */
    @Override
    protected void do_PUT(String key,V value) {
        if(remoteCache != null) remoteCache.do_PUT(key,value);
        if(hasLocalCache()) localCache.do_PUT(key,value);
        if(config().syncLocal) broadcastManager.publish(buildCacheMessage(key,CacheMessage.TYPE_PUT));

    }

    /**
     * 批量添加缓存
     *
     * @param map 键值对映射
     */
    @Override
    protected void do_PUT_ALL(Map<String, V> map) {
        if(remoteCache != null)remoteCache.do_PUT_ALL(map);
        if(hasLocalCache()) localCache.do_PUT_ALL(map);
        if(config().syncLocal) broadcastManager.publish(buildCacheMessage(map.keySet(),CacheMessage.TYPE_PUT_ALL));
    }

    /**
     * 移除缓存
     *
     * @param key 键
     * @return 是否移除成功
     */
    @Override
    protected boolean do_REMOVE(String key) {
        boolean result = false;
        if(remoteCache != null) result = remoteCache.do_REMOVE(key);
        if(hasLocalCache()) localCache.do_REMOVE(key);
        if(config().syncLocal) broadcastManager.publish(buildCacheMessage(key,CacheMessage.TYPE_REMOVE));
        return result;
    }

    /**
     * 批量移除缓存
     *
     * @param keys 键集合
     */
    @Override
    protected void do_REMOVE_ALL(Set<String> keys) {
        if(remoteCache != null)remoteCache.do_REMOVE_ALL(keys);
        if(hasLocalCache()) localCache.do_REMOVE_ALL(keys);
        if(config().syncLocal) broadcastManager.publish(buildCacheMessage(keys,CacheMessage.TYPE_REMOVE_ALL));
    }

    /**
     * 判断是否配置了本地缓存
     *
     * @return 是否有本地缓存
     */
    public boolean hasLocalCache(){
        return config().getCacheType().equals(CacheType.BOTH) || config().getCacheType().equals(CacheType.LOCAL);
    }

    /**
     * 判断是否只使用本地缓存
     *
     * @return 是否只使用本地缓存
     */
    private boolean onlyLocalCache(){
        return config().getCacheType().equals(CacheType.LOCAL);
    }

    /**
     * 从缓存加载器加载数据
     * <p>使用分段锁防止缓存击穿</p>
     *
     * @param stringKey 转换后的键
     * @param key 原始键
     * @return 加载的值
     * @throws Throwable 加载异常
     */
    protected V load(String stringKey,K key) throws Throwable {
        if(stringKey == null) return null;
        if(!config().loadLock){
           return config().getLoader().load(key);
        }
        V result = null;
        int hashCode = stringKey.hashCode();
        hashCode = hashCode<0? -hashCode :hashCode;
        ReentrantLock loadLock = loadLockMap.get(hashCode%config.loadLockShards);
        try{
            loadLock.lock();
            result = config().getLoader().load(key);
        }finally {
            loadLock.unlock();
        }
        return result;
    }

    /**
     * 批量从缓存加载器加载数据
     * <p>使用分段锁防止并发加载</p>
     *
     * @param stringKeySet 转换后的键集合
     * @param keys 原始键集合
     * @return 加载的键值对映射
     * @throws Throwable 加载异常
     */
    protected Map<K,V> loadAll(Set<String> stringKeySet,Set<K> keys) throws Throwable{
        if(CollectionUtils.isEmpty(keys)) return null;
        if(!config().loadLock){
            return config().getLoader().loadAll(keys);
        }
        String [] keyArray = stringKeySet.toArray(new String[0]);
        Arrays.sort(keyArray);
        int hashCode = Arrays.hashCode(keyArray);
        hashCode = hashCode<0? -hashCode :hashCode;
        Map<K,V> result;
        ReentrantLock loadLock = loadLockMap.get(hashCode%config.loadLockShards);
        try{
            loadLock.lock();
            result = config().getLoader().loadAll(keys);
        }finally {
            loadLock.unlock();
        }
        return result;
    }

    /**
     * 构建缓存消息
     *
     * @param key 键
     * @param type 消息类型
     * @return 缓存消息
     */
    private CacheMessage buildCacheMessage(String key,int type){
        CacheMessage cacheMessage = new CacheMessage();
        cacheMessage.setCacheName(config().name);
        cacheMessage.setKeys(new String[]{key});
        cacheMessage.setType(type);
        return cacheMessage;
    }
    /**
     * 构建批量缓存消息
     *
     * @param keys 键集合
     * @param type 消息类型
     * @return 缓存消息
     */
    private CacheMessage buildCacheMessage(Set<String> keys,int type){
        CacheMessage cacheMessage = new CacheMessage();
        cacheMessage.setCacheName(config().name);
        cacheMessage.setKeys(keys.toArray(new String[]{}));
        cacheMessage.setType(type);
        return cacheMessage;
    }

}
