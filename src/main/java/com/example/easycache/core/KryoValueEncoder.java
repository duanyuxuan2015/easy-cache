package com.example.easycache.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import java.lang.ref.WeakReference;

/**
 * Kryo值编码器
 * <p>使用Kryo框架将对象序列化为字节数组</p>
 */
public class KryoValueEncoder extends AbstractValueEncoder {

    /** 单例实例 */
    public static final KryoValueEncoder INSTANCE = new KryoValueEncoder();

    /** 初始缓冲区大小 */
    private static final int INIT_BUFFER_SIZE = 512;

    /** Kryo线程本地变量 */
    static ThreadLocal<Object[]> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
        kryo.setRegistrationRequired(false);
        kryo.setReferences(true);
        byte[] buffer = new byte[INIT_BUFFER_SIZE];
        WeakReference<byte[]> ref = new WeakReference<>(buffer);
        return new Object[]{kryo, ref};
    });

    /**
     * 构造函数
     */
    public KryoValueEncoder() {
        super();
    }

    /**
     * 将对象编码为字节数组
     *
     * @param value 要编码的对象
     * @return 编码后的字节数组
     */
    @Override
    public byte[] apply(Object value) {
        try {
            Object[] kryoAndBuffer = kryoThreadLocal.get();
            Kryo kryo = (Kryo) kryoAndBuffer[0];
            WeakReference<byte[]> ref = (WeakReference<byte[]>) kryoAndBuffer[1];
            byte[] buffer = ref.get();
            if (buffer == null) {
                buffer = new byte[INIT_BUFFER_SIZE];
            }
            Output output = new Output(buffer, -1);

            try {
                kryo.writeClassAndObject(output, value);
                return output.toBytes();
            } finally {
                //reuse buffer if possible
                if (ref.get() == null || buffer != output.getBuffer()) {
                    ref = new WeakReference<>(output.getBuffer());
                    kryoAndBuffer[1] = ref;
                }
            }
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("Kryo Encode error. ");
            sb.append("msg=").append(e.getMessage());
            throw new CacheEncodeException(sb.toString(), e);
        }
    }

}
