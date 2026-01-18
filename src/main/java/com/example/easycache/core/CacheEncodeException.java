package com.example.easycache.core;

/**
 * 缓存编码异常
 * <p>序列化和反序列化过程中发生的异常</p>
 */
public class CacheEncodeException extends CacheException {

    private static final long serialVersionUID = -1768444197009616269L;

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause 原因
     */
    public CacheEncodeException(String message, Throwable cause) {
        super(message, cause);
    }

}
