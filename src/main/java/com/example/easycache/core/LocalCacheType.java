package com.example.easycache.core;

/**
 * 本地缓存类型枚举
 * <p>定义本地缓存的实现方式</p>
 */
public enum LocalCacheType {
    /** 堆外缓存（OHC） */
    OHC,
    /** Caffeine缓存 */
    CAFFEINE
}
