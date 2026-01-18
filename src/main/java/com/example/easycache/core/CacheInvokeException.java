package com.example.easycache.core;

/**
 * 缓存调用异常
 * <p>缓存操作过程中发生的调用异常</p>
 */
public class CacheInvokeException extends CacheException {

    private static final long serialVersionUID = -9002505061387176702L;

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause 原因
     */
    public CacheInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     *
     * @param cause 原因
     */
    public CacheInvokeException(Throwable cause) {
        super(cause);
    }

}
