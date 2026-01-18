package com.example.easycache.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EasyCacheProperties 缓存配置属性单元测试
 */
public class EasyCachePropertiesTest {

    private EasyCacheProperties properties;

    @BeforeEach
    void setUp() {
        properties = new EasyCacheProperties();
    }

    @Test
    void testDefaultValues() {
        // 测试默认值
        assertEquals(10, properties.getTaskInitDelay());
        assertEquals(10, properties.getTaskPeriod());
        assertEquals(10, properties.getConsumeCount());
        assertEquals(1000, properties.getCacheMessageSize());
        assertEquals(2000, properties.getBlockDuration());
    }

    @Test
    void testSetAndGetTaskInitDelay() {
        // 测试设置和获取任务初始化延迟
        properties.setTaskInitDelay(5);

        assertEquals(5, properties.getTaskInitDelay());
    }

    @Test
    void testSetAndGetTaskPeriod() {
        // 测试设置和获取任务执行周期
        properties.setTaskPeriod(20);

        assertEquals(20, properties.getTaskPeriod());
    }

    @Test
    void testSetAndGetConsumeCount() {
        // 测试设置和获取消费数量
        properties.setConsumeCount(50);

        assertEquals(50, properties.getConsumeCount());
    }

    @Test
    void testSetAndGetCacheMessageSize() {
        // 测试设置和获取缓存消息大小
        properties.setCacheMessageSize(5000);

        assertEquals(5000, properties.getCacheMessageSize());
    }

    @Test
    void testSetAndGetBlockDuration() {
        // 测试设置和获取阻塞时长
        properties.setBlockDuration(5000);

        assertEquals(5000, properties.getBlockDuration());
    }

    @Test
    void testSetZeroValues() {
        // 测试设置零值
        properties.setTaskInitDelay(0);
        properties.setTaskPeriod(0);
        properties.setConsumeCount(0);
        properties.setCacheMessageSize(0);
        properties.setBlockDuration(0);

        assertEquals(0, properties.getTaskInitDelay());
        assertEquals(0, properties.getTaskPeriod());
        assertEquals(0, properties.getConsumeCount());
        assertEquals(0, properties.getCacheMessageSize());
        assertEquals(0, properties.getBlockDuration());
    }

    @Test
    void testSetLargeValues() {
        // 测试设置大值
        properties.setTaskInitDelay(3600);
        properties.setTaskPeriod(3600);
        properties.setConsumeCount(10000);
        properties.setCacheMessageSize(100000);
        properties.setBlockDuration(60000);

        assertEquals(3600, properties.getTaskInitDelay());
        assertEquals(3600, properties.getTaskPeriod());
        assertEquals(10000, properties.getConsumeCount());
        assertEquals(100000, properties.getCacheMessageSize());
        assertEquals(60000, properties.getBlockDuration());
    }

    @Test
    void testSetNegativeValues() {
        // 测试设置负值（虽然不推荐，但测试行为）
        properties.setTaskInitDelay(-1);

        assertEquals(-1, properties.getTaskInitDelay());
    }

    @Test
    void testMultiplePropertyChanges() {
        // 测试多次属性修改
        properties.setTaskInitDelay(5);
        assertEquals(5, properties.getTaskInitDelay());

        properties.setTaskInitDelay(15);
        assertEquals(15, properties.getTaskInitDelay());

        properties.setTaskInitDelay(30);
        assertEquals(30, properties.getTaskInitDelay());
    }

    @Test
    void testPropertyIndependence() {
        // 测试属性独立性
        properties.setTaskInitDelay(100);
        properties.setTaskPeriod(200);
        properties.setConsumeCount(300);

        assertEquals(100, properties.getTaskInitDelay());
        assertEquals(200, properties.getTaskPeriod());
        assertEquals(300, properties.getConsumeCount());
    }
}
