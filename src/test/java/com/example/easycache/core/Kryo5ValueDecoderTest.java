package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kryo5ValueDecoder 单元测试
 * <p>测试Kryo5值解码器的反序列化功能</p>
 */
public class Kryo5ValueDecoderTest {

    @Test
    void testDecodeString() {
        // 测试字符串解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        String original = "Hello, World!";
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeInteger() {
        // 测试整数解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        Integer original = 12345;
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeLong() {
        // 测试长整数解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        Long original = 123456789L;
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeDouble() {
        // 测试双精度浮点数解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        Double original = 3.14159;
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeBoolean() {
        // 测试布尔值解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        Boolean original = true;
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeList() {
        // 测试列表解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

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
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        Map<String, Integer> original = new HashMap<>();
        original.put("key1", 100);
        original.put("key2", 200);
        original.put("key3", 300);

        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertInstanceOf(Map.class, decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeNull() {
        // 测试null值解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        byte[] encoded = encoder.apply(null);
        Object decoded = decoder.doApply(encoded);

        assertNull(decoded);
    }

    @Test
    void testDecodeEmptyString() {
        // 测试空字符串解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        String original = "";
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeEmptyList() {
        // 测试空列表解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        List<String> original = new ArrayList<>();
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertInstanceOf(List.class, decoded);
        assertTrue(((List<?>) decoded).isEmpty());
    }

    @Test
    void testDecodeEmptyMap() {
        // 测试空Map解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        Map<String, String> original = new HashMap<>();
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertInstanceOf(Map.class, decoded);
        assertTrue(((Map<?, ?>) decoded).isEmpty());
    }

    @Test
    void testDecodeComplexObject() {
        // 测试复杂对象解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        Map<String, Object> original = new HashMap<>();
        original.put("name", "Test");
        original.put("age", 25);
        original.put("active", true);
        original.put("score", 95.5);

        List<String> hobbies = new ArrayList<>();
        hobbies.add("reading");
        hobbies.add("coding");
        original.put("hobbies", hobbies);

        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertInstanceOf(Map.class, decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeChineseCharacters() {
        // 测试中文字符解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        String original = "你好，世界！";
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeLargeString() {
        // 测试大字符串解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("test");
        }
        String original = sb.toString();
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeSpecialCharacters() {
        // 测试特殊字符解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        String original = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeNegativeNumber() {
        // 测试负数解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        Integer original = -12345;
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testDecodeZero() {
        // 测试零值解码
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        Integer original = 0;
        byte[] encoded = encoder.apply(original);
        Object decoded = decoder.doApply(encoded);

        assertNotNull(decoded);
        assertEquals(original, decoded);
    }

    @Test
    void testEncodeDecodeRoundTrip() {
        // 测试编码解码往返
        Kryo5ValueDecoder decoder = Kryo5ValueDecoder.INSTANCE;
        Kryo5ValueEncoder encoder = Kryo5ValueEncoder.INSTANCE;

        // 测试各种数据类型的往返
        String stringValue = "Test String";
        Integer intValue = 42;
        Double doubleValue = 3.14159;
        Boolean boolValue = false;

        assertEquals(stringValue, decoder.doApply(encoder.apply(stringValue)));
        assertEquals(intValue, decoder.doApply(encoder.apply(intValue)));
        assertEquals(doubleValue, decoder.doApply(encoder.apply(doubleValue)));
        assertEquals(boolValue, decoder.doApply(encoder.apply(boolValue)));
    }
}
