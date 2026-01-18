
package com.example.easycache.core;

import java.time.Duration;
import java.util.function.Function;

/**
 * 缓存配置类
 * <p>定义缓存的各种配置参数，包括过期时间、缓存类型、序列化器等</p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class CacheConfig<K,V> {
    /** 缓存名称 */
    protected String name;
    /** 远程缓存过期时间 */
    protected Duration expire;
    /** 本地缓存过期时间 */
    protected Duration localExpire;
    /** 本地缓存过期类型 */
    protected ExpireType localExpireType;
    /** 本地缓存最大条目数 */
    protected Integer localLimit;
    /** 缓存类型（本地、远程或两者） */
    protected CacheType cacheType;
    /** 本地缓存实现类型 */
    protected LocalCacheType localCacheType;
    /** 键转换器 */
    protected KeyConvertor<K> keyConvertor;
    /** 值编码器 */
    protected Function<Object, byte[]> valueEncoder;
    /** 值解码器 */
    protected Function<byte[], Object> valueDecoder;
    /** 缓存的空值对象，用于防止缓存穿透 */
    protected V cacheNullValue;
    /** 空值过期时间 */
    protected Duration nullValueExpire;
    /** 缓存加载器 */
    protected CacheLoader<K, V> loader;
    /** 是否使用加载锁防止缓存击穿 */
    protected Boolean loadLock;
    /** 加载锁的分片数量 */
    protected Integer loadLockShards;
    /** 是否同步本地缓存到集群 */
    protected Boolean syncLocal;
    /** 是否使用堆外内存 */
    protected Boolean offHeap;
    /** 缓冲区释放延迟时间 */
    protected Long bufReleaseDelay;


    protected CacheConfig() {
    }

    public String getName() {
        return name;
    }

    public Duration getExpire() {
        return expire;
    }

    public Duration getLocalExpire() {
        return localExpire;
    }

    public CacheType getCacheType() {
        return cacheType;
    }

    public Integer getLocalLimit() {
        return localLimit;
    }

    public Boolean getSyncLocal() {
        return syncLocal;
    }

    public Function<Object, byte[]> getValueEncoder() {
        return valueEncoder;
    }

    public KeyConvertor<K> getKeyConvertor() {
        return keyConvertor;
    }

    public Function<byte[], Object> getValueDecoder() {
        return valueDecoder;
    }

    public  V getCacheNullValue() {
        return cacheNullValue;
    }

    public Duration getNullValueExpire() {
        return nullValueExpire;
    }

    public Boolean getLoadLock(){
        return loadLock;
    }

    public CacheLoader<K, V> getLoader() {
        return loader;
    }

    public LocalCacheType getLocalCacheType() {
        return localCacheType;
    }

    public Boolean getOffHeap() {
        return offHeap;
    }

    public void setOffHeap(Boolean offHeap) {
        this.offHeap = offHeap;
    }
    public Long getBufReleaseDelay() {
        return bufReleaseDelay;
    }

    public void setBufReleaseDelay(Long bufReleaseDelay) {
        this.bufReleaseDelay = bufReleaseDelay;
    }

    public Integer getLoadLockShards() {
        return loadLockShards;
    }

    public void setLoadLockShards(Integer loadLockShards) {
        this.loadLockShards = loadLockShards;
    }

    public ExpireType getLocalExpireType() {
        return localExpireType;
    }

    public void setLocalExpireType(ExpireType localExpireType) {
        this.localExpireType = localExpireType;
    }
}
