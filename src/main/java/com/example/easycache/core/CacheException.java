package com.example.easycache.core;

/**
 * 缓存异常
 * <p>缓存操作过程中的基础异常类</p>
 */
public class CacheException extends RuntimeException {
    private static final long serialVersionUID = -9066209768410752667L;

    /**
     * 构造函数
     *
     * @param message 异常消息
     */
    public CacheException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause 原因
     */
    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     *
     * @param cause 原因
     */
    public CacheException(Throwable cause) {
        super(cause);
    }
}
