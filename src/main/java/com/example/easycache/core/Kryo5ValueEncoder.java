package com.example.easycache.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Kryo5值编码器
 * <p>使用Kryo5框架将对象序列化为字节数组</p>
 */
public class Kryo5ValueEncoder extends AbstractValueEncoder {

    /** 单例实例 */
    public static final Kryo5ValueEncoder INSTANCE = new Kryo5ValueEncoder();

    /**
     * 构造函数
     */
    public Kryo5ValueEncoder() {
        super();
    }

    /** Kryo对象池 */
    static final Pool<Kryo> kryoPool  = new Pool<>(true, false, 64) {
            protected Kryo create() {
                Kryo kryo = new Kryo();
                kryo.setRegistrationRequired(false);
                kryo.setReferences(true);
                kryo.register(List.class);
                kryo.register(ArrayList.class);
                return kryo;
            }
    };

    /**
     * 将对象编码为字节数组
     *
     * @param value 要编码的对象
     * @return 编码后的字节数组
     */
    @Override
    public byte[] apply(Object value) {
        Kryo kryo = kryoPool.obtain();
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); Output output = new Output(byteOut)) {
            kryo.writeClassAndObject(output, value);
            output.flush();
            return byteOut.toByteArray();
        } catch (Exception e) {
            throw new CacheEncodeException("Kryo Encode error. " + "msg=" + e.getMessage(), e);
        } finally {
            kryoPool.free(kryo);
        }
    }

}
