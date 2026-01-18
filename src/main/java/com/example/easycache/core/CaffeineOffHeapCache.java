package com.example.easycache.core;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine堆外缓存实现
 * <p>使用Caffeine作为索引，Netty堆外内存存储数据</p>
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class CaffeineOffHeapCache<K, V> extends AbstractCache<K, V> {

    /**
     * 延迟ByteBuf任务
     * <p>用于延迟释放ByteBuf资源</p>
     */
    public static class DelayedByteBufTask implements Delayed {
        private final long executeTime; // 任务执行时间戳
        private final ByteBuf buf;      // 待处理的ByteBuf数据

        public DelayedByteBufTask(ByteBuf buf, long delay, TimeUnit unit) {
            this.buf = buf;
            this.executeTime = System.currentTimeMillis() + unit.toMillis(delay);
        }

        // 获取剩余延迟时间
        @Override
        public long getDelay(TimeUnit unit) {
            long remaining = executeTime - System.currentTimeMillis();
            return unit.convert(remaining, TimeUnit.MILLISECONDS);
        }

        // 用于DelayedQueue排序
        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.executeTime, ((DelayedByteBufTask) o).executeTime);
        }

        // 释放ByteBuf资源
        public void release() {
            NettyMemoryPool.releaseBuffer( buf);
        }
    }

    DelayQueue<DelayedByteBufTask> delayQueue = new DelayQueue<>();

    private final com.github.benmanes.caffeine.cache.Cache<String, ByteBuf> caffineCache;
    private final java.util.concurrent.ScheduledExecutorService cleanupExecutor;

    public CaffeineOffHeapCache(CacheConfig<K, V> config) {
        super(config);
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(config.getLocalLimit() == null ? CacheConstants.DEFAULT_LOCAL_LIMIT : config.getLocalLimit());
        long cacheTTL = config.localExpire != null ?config.localExpire.toNanos():Long.MAX_VALUE;
        Expiry<Object, Object> customExpiry = new Expiry<>() {
            // 1. 缓存创建时：设置默认TTL（30秒）
            @Override
            public long expireAfterCreate(Object key, Object value, long currentTime) {
                if(config.nullValueExpire != null){
                    if(value.equals(config.cacheNullValue)){
                        return config.nullValueExpire.toNanos();
                    }
                }
                return cacheTTL;
            }

            // 2. 缓存更新时：重置为60秒TTL
            @Override
            public long expireAfterUpdate(Object key, Object value, long currentTime, long currentDuration) {

                if(config.localExpireType.equals(ExpireType.AFTER_WRITE)){
                    return currentDuration;
                }

                if(config.nullValueExpire != null){
                    if(value.equals(config.cacheNullValue)){
                        return config.nullValueExpire.toNanos();
                    }
                }
                return cacheTTL;
            }

            // 3. 缓存访问时：续命10秒（原剩余TTL + 10秒，不超过默认TTL上限）
            @Override
            public long expireAfterRead(Object key, Object value, long currentTime, long currentDuration) {
                if(config.localExpireType.equals(ExpireType.AFTER_WRITE)){
                    return currentDuration;
                }
                if(config.nullValueExpire != null){
                    if(value.equals(config.cacheNullValue)){
                        return currentDuration;
                    }
                }
                return cacheTTL;
            }
        };
        caffeine.expireAfter(customExpiry);
        caffeine.removalListener((key, buf, cause) -> {
            // 缓存淘汰时释放堆外缓冲区
            delayQueue.add(new DelayedByteBufTask((ByteBuf) buf,config.bufReleaseDelay,TimeUnit.MILLISECONDS));
        });
        caffineCache = caffeine.build();

        // 创建专用的清理线程执行器，不使用共享的defaultExecutor
        cleanupExecutor = java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "CaffeineOffHeapCache-Cleanup-" + config.getName());
            t.setDaemon(true);
            return t;
        });
        cleanupExecutor.submit(()->{
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    DelayedByteBufTask task = delayQueue.take(); // 阻塞直到有到期任务
                    task.release(); // 确保释放ByteBuf
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    protected V do_GET(K key, String newKey) {
        V result = null;
        ByteBuf bufDuplicate = null;
        ByteBuf buf = caffineCache.getIfPresent(newKey);
        if (buf == null || buf.refCnt() <= 0) return null;
        bufDuplicate = buf.duplicate();
        byte[] bytes = new byte[bufDuplicate.readableBytes()];
        bufDuplicate.readBytes(bytes);
        result = (V) Kryo5ValueDecoder.INSTANCE.doApply(bytes);
        return result;
    }

    @Override
    protected Map<K, V> do_GET_ALL(Map<String, K> KeyMap) {
        Map<K, V> resultMap = new HashMap<>();
        // 为所有请求的键添加结果，不存在的键返回null
        for (Map.Entry<String, K> entry : KeyMap.entrySet()) {
            String key = entry.getKey();
            K originalKey = entry.getValue();
            V result = null;
            ByteBuf buf = caffineCache.getIfPresent(key);
            if (buf != null && buf.refCnt() > 0) {
                ByteBuf bufDuplicate = buf.duplicate();
                byte[] bytes = new byte[bufDuplicate.readableBytes()];
                bufDuplicate.readBytes(bytes);
                result = (V) Kryo5ValueDecoder.INSTANCE.doApply(bytes);
            }
            resultMap.put(originalKey, result);
        }
        return resultMap;
    }

    @Override
    protected void do_PUT(String key, V value) {
        ByteBuf buf = toByteBuf(value);
        caffineCache.put(key, buf);
    }

    @Override
    protected void do_PUT_ALL(Map<String, V> map) {
        Map<String, ByteBuf> notNullMap = new HashMap<>(map.size());
        Map<String, ByteBuf> nullValueMap = new HashMap<>();
        map.forEach((key, value) -> {
            if (config().cacheNullValue == null || value != null) {
                notNullMap.put(key, toByteBuf(value));
            } else {
                nullValueMap.put(key, toByteBuf(config().cacheNullValue));
            }
        });
        if (!notNullMap.isEmpty()) caffineCache.putAll(notNullMap);
        if (nullValueMap.isEmpty()) return;
        caffineCache.putAll(nullValueMap);
    }

    private ByteBuf toByteBuf(V value) {
        byte[] bytes = Kryo5ValueEncoder.INSTANCE.apply(value);
        // 从内存池分配堆外缓冲区
        ByteBuf buf = NettyMemoryPool.allocateDirectBuffer(bytes.length);
        buf.writeBytes(bytes);
        return buf;
    }

    @Override
    protected boolean do_REMOVE(String key) {
        boolean keyExist = caffineCache.getIfPresent(key) != null;
        caffineCache.invalidate(key);
        return keyExist;
    }

    @Override
    protected void do_REMOVE_ALL(Set<String> keys) {
        caffineCache.invalidateAll(keys);
    }
}


