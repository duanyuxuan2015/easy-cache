package com.example.easycache.core;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 * <p>基于Redis实现的分布式锁</p>
 */
public class DistributedLock {

    /** Redis模板 */
    private final RedisTemplate<String, byte[]> redisTemplate;

    /**
     * 构造函数
     *
     * @param redisTemplate Redis模板
     */
    public DistributedLock(RedisTemplate<String, byte[]> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey 锁键名
     * @param lockValue 锁值
     * @param duration 过期时间
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String lockValue,Duration duration) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue.getBytes(StandardCharsets.UTF_8), duration.toMillis(), TimeUnit.MILLISECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁键名
     */
    public void unlock(String lockKey) {
        redisTemplate.delete(lockKey);
    }
}
