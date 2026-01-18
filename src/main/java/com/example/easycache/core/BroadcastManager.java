package com.example.easycache.core;


import com.alibaba.fastjson2.JSON;
import io.lettuce.core.RedisCommandInterruptedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 广播管理器
 * <p>负责集群间缓存同步的广播功能</p>
 */
public class BroadcastManager implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(MultiLevelCache.class);

    /** 消息源ID */
    private final String sourceId = UUID.randomUUID().toString();

    /** 缓存配置属性 */
    private final EasyCacheProperties easyCacheProperties;
    /** 缓存管理器 */
    private final CacheManager cacheManager;
    /** Redis模板 */
    private final RedisTemplate<String, byte[]> redisTemplate;
    /** 流键名 */
    private static final String STREAM_KEY = "easy-cache-stream";
    /** 当前记录ID */
    private String currentRecordId = null;

    /**
     * 构造函数
     *
     * @param cacheManager 缓存管理器
     * @param easyCacheProperties 缓存配置属性
     * @param redisTemplate Redis模板
     */
    public BroadcastManager(CacheManager cacheManager, EasyCacheProperties easyCacheProperties,RedisTemplate<String, byte[]> redisTemplate) {
        this.cacheManager = cacheManager;
        this.redisTemplate = redisTemplate;
        this.easyCacheProperties = easyCacheProperties;
    }

    /**
     * 发布缓存消息
     *
     * @param cacheMessage 缓存消息
     */
    public void publish(CacheMessage cacheMessage) {
        cacheMessage.setSourceId(sourceId);
        StreamOperations<String, String, CacheMessage> streamOps = redisTemplate.opsForStream();
        Map<String, CacheMessage> message = new HashMap<>();
        message.put("message", cacheMessage);
        streamOps.add(STREAM_KEY, message);
    }

    /**
     * 开始订阅消息
     */
    public void startSubscribe() {
        EasyCacheExecutor.defaultExecutor().execute(this::processNotification);
        EasyCacheExecutor.defaultExecutor().scheduleAtFixedRate(this::trimCacheMessages, easyCacheProperties.getTaskInitDelay(),easyCacheProperties.getTaskPeriod(), TimeUnit.SECONDS);
    }

    @Override
    public void close() throws Exception {
    }

    /**
     * 处理通知消息
     */
    protected void processNotification() {

        StreamOperations<String, String, CacheMessage> streamOps = redisTemplate.opsForStream();

        StreamOffset<String> streamOffset = StreamOffset.create(STREAM_KEY, ReadOffset.latest());

        while (true) { // 循环消费（实际项目建议用线程池或定时任务，避免死循环）
            try {
                // COUNT 1：每次消费 1 条消息；BLOCK 5000：阻塞 5 秒（单位：毫秒）；>：表示消费组内未分配的消息
                if (null != currentRecordId) {
                    streamOffset = StreamOffset.create(STREAM_KEY, ReadOffset.from(currentRecordId));
                }
                List<MapRecord<String, String, CacheMessage>> records = streamOps.read(
                        StreamReadOptions.empty().count(easyCacheProperties.getConsumeCount()).block(Duration.ofMillis(easyCacheProperties.getBlockDuration())), streamOffset);
                // 4. 处理消息（若有消息）
                if (records != null && !records.isEmpty()) {
                    for (MapRecord<String, String, CacheMessage> record : records) {
                        // 获取消息 ID 和内容
                        String messageId = record.getId().getValue();
                        Map<String, CacheMessage> messageContent = record.getValue();
                        currentRecordId = record.getId().getValue();
                        // 业务处理（示例：打印订单信息）
                        logger.info("Message：ID=" + messageId + ", Content=" + JSON.toJSONString(messageContent));
                        processCacheMessage(messageContent.get("message"));
                    }
                }
            } catch (Exception e) {
                if(e.getCause() instanceof RedisCommandInterruptedException){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        continue;
                    }
                }
                logger.error("failed to read stream message", e);
            }
        }

    }

    /**
     * 处理缓存消息
     *
     * @param cacheMessage 缓存消息
     */
    private void processCacheMessage(CacheMessage cacheMessage) {
        if (sourceId.equals(cacheMessage.getSourceId())) {
            return;
        }
        Cache cache = cacheManager.getCache(cacheMessage.getCacheName());
        if (cache == null) {
            logger.warn("Cache instance not exists: {}", cacheMessage.getCacheName());
            return;
        }

        if (!(cache instanceof MultiLevelCache)) {
            logger.error("Cache instance is not MultiLevelCache: {}", cacheMessage.getCacheName());
            return;
        }
        MultiLevelCache multiLevelCache = (MultiLevelCache) cache;
        Set<String> keys = Stream.of(cacheMessage.getKeys()).collect(Collectors.toSet());
        multiLevelCache.getLocalCache().do_REMOVE_ALL(keys);
        logger.info("remove keys from local cache : {} {}", cache.config().name, JSON.toJSONString(keys));
    }

    /**
     * 清理缓存消息
     */
    private void trimCacheMessages() {
        DistributedLock distributedLock = new DistributedLock(redisTemplate);
        // 生成唯一value（防止释放其他线程的锁）
        String lockValue = UUID.randomUUID().toString();
        boolean locked = false;
        try {
            locked = distributedLock.tryLock(STREAM_KEY+"-lock", lockValue, Duration.ofMillis(2000));
            if(!locked) return;
            StreamOperations<String, String, CacheMessage> streamOps = redisTemplate.opsForStream();
            streamOps.trim(STREAM_KEY,easyCacheProperties.getCacheMessageSize(),true);
            logger.debug("succeed to trim cache messages");
        }catch (Exception ex){
            logger.error("failed to trim cache message",ex);
        } finally {
            if(locked) distributedLock.unlock(STREAM_KEY+"-lock");
        }
    }

}
