package com.example.easycache.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LocalCacheType 枚举单元测试
 */
public class LocalCacheTypeTest {

    @Test
    void testLocalCacheTypeValues() {
        // 测试本地缓存类型枚举值
        assertNotNull(LocalCacheType.OHC);
        assertNotNull(LocalCacheType.CAFFEINE);
    }

    @Test
    void testLocalCacheTypeCount() {
        // 测试本地缓存类型数量
        LocalCacheType[] types = LocalCacheType.values();
        assertEquals(2, types.length);
    }
}
