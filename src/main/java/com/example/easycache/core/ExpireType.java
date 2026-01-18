package com.example.easycache.core;

/**
 * 过期类型枚举
 * <p>定义缓存的过期策略</p>
 */
public enum ExpireType {
    /** 写入后过期 */
    AFTER_WRITE,
    /** 访问后过期 */
    AFTER_ACCESS
}
