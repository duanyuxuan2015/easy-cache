package com.example.easycache.core;

import java.util.function.Function;

/**
 * 抽象值解码器
 * <p>将字节数组解码为对象的基类</p>
 */
public abstract class AbstractValueDecoder implements Function<byte[], Object> {

    public AbstractValueDecoder() {
    }

    /**
     * 执行解码操作
     *
     * @param buffer 字节数组
     * @return 解码后的对象
     * @throws Exception 解码异常
     */
    protected abstract Object doApply(byte[] buffer) throws Exception;

    /**
     * 解码字节数组
     *
     * @param buffer 字节数组
     * @return 解码后的对象
     */
    @Override
    public Object apply(byte[] buffer) {
        try {
            return doApply(buffer);
        } catch (Throwable e) {
            throw new CacheEncodeException("decode error", e);
        }
    }


}
