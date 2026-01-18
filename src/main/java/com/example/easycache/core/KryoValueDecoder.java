package com.example.easycache.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

import java.io.ByteArrayInputStream;

/**
 * Kryo值解码器
 * <p>使用Kryo框架将字节数组反序列化为对象</p>
 */
public class KryoValueDecoder extends AbstractValueDecoder {

    /** 单例实例 */
    public static final KryoValueDecoder INSTANCE = new KryoValueDecoder();

    /**
     * 构造函数
     */
    public KryoValueDecoder() {
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
        ByteArrayInputStream in;
        in = new ByteArrayInputStream(buffer);
        Input input = new Input(in);
        Kryo kryo = (Kryo) KryoValueEncoder.kryoThreadLocal.get()[0];
        kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
        return kryo.readClassAndObject(input);
    }
}
