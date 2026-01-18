package com.example.easycache.core;

/**
 * 缓存类型枚举
 * <p>定义缓存的类型：远程、本地或两者</p>
 */
public enum CacheType {
    /** 远程缓存 */
    REMOTE,
    /** 本地和远程缓存 */
    BOTH,
    /** 本地缓存 */
    LOCAL
}
