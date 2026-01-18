package com.example.easycache.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

import java.io.ByteArrayInputStream;

/**
 * Kryo5值解码器
 * <p>使用Kryo5框架将字节数组反序列化为对象</p>
 */
public class Kryo5ValueDecoder extends AbstractValueDecoder {

    /** 单例实例 */
    public static final Kryo5ValueDecoder INSTANCE = new Kryo5ValueDecoder();

    /**
     * 构造函数
     */
    public Kryo5ValueDecoder() {
        super();
    }

    /**
     * 将字节数组解码为对象
     *
     * @param buffer 字节数组
     * @return 解码后的对象
     */
    @Override
    public Object doApply(byte[] buffer) {
        Object result;
        Kryo kryo = Kryo5ValueEncoder.kryoPool.obtain();
        try {
            ByteArrayInputStream in;
            in = new ByteArrayInputStream(buffer);
            Input input = new Input(in);
            kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
            result = kryo.readClassAndObject(input);
        } catch (Exception e) {
            throw new CacheEncodeException("Kryo Decode error. " + "msg=" + e.getMessage(), e);
        } finally {
            Kryo5ValueEncoder.kryoPool.free(kryo);
        }
        return result;
    }
}
