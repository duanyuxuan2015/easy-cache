package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KryoValueDecoder Kryo值解码器单元测试
 */
public class KryoValueDecoderTest {

    @Test
    void testDecodeString() {
        // 测试字符串解码
        KryoValueDecoder decoder = KryoValueDecoder.INSTANCE;
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;

        String original = "Hello, World!";
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeInteger() {
        // 测试整数解码
        KryoValueDecoder decoder = KryoValueDecoder.INSTANCE;
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;

        Integer original = 12345;
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeNull() {
        // 测试null值解码
        KryoValueDecoder decoder = KryoValueDecoder.INSTANCE;
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;

        byte[] encoded = encoder.apply(null);
        Object decoded = decoder.doApply(encoded);

        assertNull(decoded);
    }

    @Test
    void testDecodeList() {
        // 测试列表解码
        KryoValueDecoder decoder = KryoValueDecoder.INSTANCE;
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;

        List<String> original = new ArrayList<>();
        original.add("item1");
        original.add("item2");
        original.add("item3");

        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertInstanceOf(List.class, decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeMap() {
        // 测试Map解码
        KryoValueDecoder decoder = KryoValueDecoder.INSTANCE;
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;

        Map<String, Integer> original = new HashMap<>();
        original.put("key1", 100);
        original.put("key2", 200);

        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertInstanceOf(Map.class, decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeChineseCharacters() {
        // 测试中文字符解码
        KryoValueDecoder decoder = KryoValueDecoder.INSTANCE;
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;

        String original = "你好，世界！";
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeSpecialCharacters() {
        // 测试特殊字符解码
        KryoValueDecoder decoder = KryoValueDecoder.INSTANCE;
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;

        String original = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testSingletonInstance() {
        // 测试单例实例
        KryoValueDecoder instance1 = KryoValueDecoder.INSTANCE;
        KryoValueDecoder instance2 = KryoValueDecoder.INSTANCE;

        assertSame(instance1, instance2);
    }

    @Test
    void testDecodeConsistency() {
        // 测试解码一致性
        KryoValueDecoder decoder = KryoValueDecoder.INSTANCE;
        KryoValueEncoder encoder = KryoValueEncoder.INSTANCE;

        String original = "test";
        byte[] encoded = encoder.apply(original);
        Object decoded1 = decoder.doApply(encoded);
        Object decoded2 = decoder.doApply(encoded);

        assertEquals(decoded1, decoded2);
    }
}
