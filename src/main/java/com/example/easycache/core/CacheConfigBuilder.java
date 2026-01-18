package com.example.easycache.core;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

/**
 * 缓存配置构建器
 * <p>使用建造者模式构建缓存配置对象</p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class CacheConfigBuilder<K,V> {
    /** 缓存名称 */
    private final String name;
    /** 远程缓存过期时间 */
    private Duration expire;
    /** 本地缓存过期时间 */
    private Duration localExpire;
    /** 本地缓存过期类型 */
    private ExpireType localExpireType = ExpireType.AFTER_WRITE;
    /** 本地缓存最大条目数 */
    private Integer localLimit;
    /** 缓存类型 */
    private CacheType cacheType;
    /** 本地缓存类型 */
    private LocalCacheType localCacheType;
    /** 是否同步本地缓存 */
    private Boolean syncLocal;
    /** 键转换器 */
    private KeyConvertor<K> keyConvertor;
    /** 值编码器 */
    private Function<Object, byte[]> valueEncoder;
    /** 值解码器 */
    private Function<byte[], Object> valueDecoder;
    /** 空值对象 */
    private V cacheNullValue;
    /** 空值过期时间 */
    private Duration nullValueExpire;
    /** 是否使用加载锁 */
    private Boolean loadLock=false;
    /** 加载锁分片数 */
    private Integer loadLockShards;
    /** 缓存加载器 */
    private CacheLoader<K,V> loader;
    /** 是否使用堆外内存 */
    private Boolean offHeap =false;
    /** 缓冲区释放延迟 */
    private Long bufReleaseDelay = 5000L;

    /**
     * 构造函数
     *
     * @param name 缓存名称
     */
    public CacheConfigBuilder(String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    /**
     * 构建缓存配置对象
     *
     * @return 缓存配置对象
     */
    public CacheConfig<K,V> build() {
        CacheConfig<K,V> c = new CacheConfig<>();
        c.name = name;
        c.expire = expire;
        c.localExpire = localExpire;
        c.localExpireType = localExpireType;
        c.localLimit = localLimit;
        c.cacheType = cacheType;
        c.syncLocal = syncLocal;
        c.valueEncoder = valueEncoder == null ? Kryo5ValueEncoder.INSTANCE : valueEncoder;
        c.valueDecoder = valueDecoder == null ? Kryo5ValueDecoder.INSTANCE : valueDecoder;
        c.cacheNullValue = cacheNullValue;
        c.nullValueExpire = nullValueExpire;
        c.keyConvertor = keyConvertor;
        c.loader = loader;
        c.loadLock = loadLock;
        c.loadLockShards = loadLockShards;
        c.localCacheType = localCacheType==null ? LocalCacheType.CAFFEINE : localCacheType;
        c.offHeap = offHeap;
        c.bufReleaseDelay = bufReleaseDelay;
        return c;
    }

    /**
     * 设置远程缓存过期时间
     *
     * @param expire 过期时间
     * @return this
     */
    public CacheConfigBuilder<K,V> expire(Duration expire) {
        this.expire = expire;
        return this;
    }

    /**
     * 设置本地缓存过期时间
     *
     * @param localExpire 过期时间
     * @return this
     */
    public CacheConfigBuilder<K,V> localExpire(Duration localExpire) {
        this.localExpire = localExpire;
        return this;
    }

    /**
     * 设置本地缓存过期类型
     *
     * @param expireType 过期类型
     * @return this
     */
    public CacheConfigBuilder<K,V> localExpireType(ExpireType expireType) {
        this.localExpireType = expireType;
        return this;
    }

    /**
     * 设置空值过期时间
     *
     * @param nullValueExpire 空值过期时间
     * @return this
     */
    public CacheConfigBuilder<K,V> nullValueExpire(Duration nullValueExpire) {
        this.nullValueExpire = nullValueExpire;
        return this;
    }

    /**
     * 启用加载锁防止缓存击穿
     *
     * @param loadLockShards 锁分片数量
     * @return this
     */
    public CacheConfigBuilder<K,V> loadLock(Integer loadLockShards) {
        this.loadLock = true;
        this.loadLockShards = loadLockShards;
        return this;
    }


    /**
     * 设置本地缓存最大条目数
     *
     * @param localLimit 最大条目数
     * @return this
     */
    public CacheConfigBuilder<K,V> localLimit(Integer localLimit) {
        this.localLimit = localLimit;
        return this;
    }

    /**
     * 设置缓存类型
     *
     * @param cacheType 缓存类型
     * @return this
     */
    public CacheConfigBuilder<K,V> cacheType(CacheType cacheType) {
        this.cacheType = cacheType;
        return this;
    }

//    public CacheConfigBuilder<K,V> localCacheType(LocalCacheType localCacheType) {
//        this.localCacheType = localCacheType;
//        return this;
//    }

    /**
     * 设置是否同步本地缓存到集群
     *
     * @param syncLocal 是否同步
     * @return this
     */
    public CacheConfigBuilder<K,V> syncLocal(Boolean syncLocal) {
        this.syncLocal = syncLocal;
        return this;
    }

    /**
     * 设置值编码器
     *
     * @param valueEncoder 值编码器
     * @return this
     */
    public CacheConfigBuilder<K,V> valueEncoder(Function<Object, byte[]> valueEncoder) {
        this.valueEncoder = valueEncoder;
        return this;
    }

    /**
     * 设置值解码器
     *
     * @param valueDecoder 值解码器
     * @return this
     */
    public CacheConfigBuilder<K,V> valueDecoder(Function<byte[], Object> valueDecoder) {
        this.valueDecoder = valueDecoder;
        return this;
    }

    /**
     * 设置空值对象
     *
     * @param cacheNullValue 空值对象
     * @return this
     */
    public CacheConfigBuilder<K,V> cacheNullValue(V cacheNullValue) {
        this.cacheNullValue = cacheNullValue;
        return this;
    }

    /**
     * 设置缓存加载器
     *
     * @param loader 缓存加载器
     * @return this
     */
    public CacheConfigBuilder<K,V> loader(CacheLoader<K, V> loader) {
        this.loader = loader;
        return this;
    }

    /**
     * 设置键转换器
     *
     * @param keyConvertor 键转换器
     * @return this
     */
    public CacheConfigBuilder<K,V> keyConvertor(KeyConvertor<K> keyConvertor) {
        this.keyConvertor = keyConvertor;
        return this;
    }

    /**
     * 设置是否使用堆外内存
     *
     * @param offHeap 是否使用堆外内存
     * @return this
     */
    public CacheConfigBuilder<K,V> offHeap(Boolean offHeap){
        this.offHeap =offHeap;
        return this;
    }

    /**
     * 设置缓冲区释放延迟
     *
     * @param bufReleaseDelay 延迟时间（毫秒）
     * @return this
     */
    public CacheConfigBuilder<K,V> bufReleaseDelay(Long bufReleaseDelay){
        this.bufReleaseDelay =bufReleaseDelay;
        return this;
    }

}