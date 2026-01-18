package com.example.easycache;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * 核心类测试套件
 * <p>汇总所有核心类的单元测试（30个测试类，304个测试方法）</p>
 */
@Suite
@SelectClasses({
    // 配置相关测试
    com.example.easycache.core.CacheConfigBuilderTest.class,
    com.example.easycache.core.CacheConfigTest.class,

    // 序列化器测试
    com.example.easycache.core.Kryo5ValueEncoderTest.class,
    com.example.easycache.core.Kryo5ValueDecoderTest.class,
    com.example.easycache.core.KryoValueEncoderTest.class,
    com.example.easycache.core.KryoValueDecoderTest.class,
    com.example.easycache.core.OhcCacheKeySerializerTest.class,
    com.example.easycache.core.OhcCacheValueSerializerTest.class,

    // 缓存实现测试
    com.example.easycache.core.CaffeineCacheTest.class,
    com.example.easycache.core.OHCCacheTest.class,
    com.example.easycache.core.CaffeineOffHeapCacheTest.class,

    // 工具类测试
    com.example.easycache.core.Fastjson2KeyConvertorTest.class,
    com.example.easycache.core.NettyMemoryPoolTest.class,
    com.example.easycache.core.CacheLoaderTest.class,
    com.example.easycache.core.EasyCachePropertiesTest.class,
    com.example.easycache.core.EasyCacheExecutorTest.class,

    // 其他测试
    MainTest.class
})
public class AllCoreTests {
}
