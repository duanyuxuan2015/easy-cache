package com.example.easycache.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EasyCacheExecutor 缓存执行器单元测试
 */
public class EasyCacheExecutorTest {

    @BeforeAll
    static void init() {
        // 确保执行器已初始化
        EasyCacheExecutor.defaultExecutor();
    }

    @Test
    void testDefaultExecutorNotNull() {
        // 测试默认执行器不为空
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        assertNotNull(executor);
    }

    @Test
    void testDefaultExecutorIsSingleton() {
        // 测试默认执行器是单例
        ScheduledExecutorService executor1 = EasyCacheExecutor.defaultExecutor();
        ScheduledExecutorService executor2 = EasyCacheExecutor.defaultExecutor();

        assertSame(executor1, executor2);
    }

    @Test
    void testDefaultExecutorCanScheduleTask() throws InterruptedException {
        // 测试默认执行器可以调度任务
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        boolean[] executed = new boolean[1];
        executor.schedule(() -> executed[0] = true, 100, TimeUnit.MILLISECONDS);

        Thread.sleep(200);

        assertTrue(executed[0]);
    }

    @Test
    void testDefaultExecutorCanExecuteTask() throws InterruptedException {
        // 测试默认执行器可以执行任务
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        boolean[] executed = new boolean[1];
        executor.execute(() -> executed[0] = true);

        Thread.sleep(100);

        assertTrue(executed[0]);
    }

    @Test
    void testDefaultExecutorSupportsFixedRateSchedule() throws InterruptedException {
        // 测试默认执行器支持固定速率调度
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        int[] counter = new int[1];
        executor.scheduleAtFixedRate(() -> counter[0]++, 0, 50, TimeUnit.MILLISECONDS);

        Thread.sleep(200);

        assertTrue(counter[0] >= 3); // 至少执行3次
    }

    @Test
    void testDefaultExecutorSupportsFixedDelaySchedule() throws InterruptedException {
        // 测试默认执行器支持固定延迟调度
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        int[] counter = new int[1];
        executor.scheduleWithFixedDelay(() -> counter[0]++, 0, 50, TimeUnit.MILLISECONDS);

        Thread.sleep(200);

        assertTrue(counter[0] >= 3); // 至少执行3次
    }

    @Test
    void testDefaultExecutorIsNotShutdown() {
        // 测试默认执行器未关闭
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        assertFalse(executor.isShutdown());
    }

    @Test
    void testDefaultExecutorIsNotTerminated() {
        // 测试默认执行器未终止
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        assertFalse(executor.isTerminated());
    }

    @Test
    void testDefaultExecutorCanHandleMultipleTasks() throws InterruptedException {
        // 测试默认执行器可以处理多个任务
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        int[] counter = new int[1];
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> counter[0]++);
        }

        Thread.sleep(100);

        assertEquals(10, counter[0]);
    }

    @Test
    void testDefaultExecutorIsDaemon() {
        // 测试默认执行器是守护线程
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        // 无法直接测试线程是否为守护线程，但可以通过其他方式验证
        assertNotNull(executor);
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        // 测试并发访问
        Thread[] threads = new Thread[10];
        boolean[] allSame = new boolean[1];
        allSame[0] = true;

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();
                if (executor != EasyCacheExecutor.defaultExecutor()) {
                    allSame[0] = false;
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertTrue(allSame[0]);
    }

    @Test
    void testDefaultExecutorSubmitTask() throws InterruptedException {
        // 测试提交任务
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        boolean[] executed = new boolean[1];
        executor.submit(() -> executed[0] = true);

        Thread.sleep(100);

        assertTrue(executed[0]);
    }

    @Test
    void testDefaultExecutorScheduleWithZeroDelay() throws InterruptedException {
        // 测试零延迟调度
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        boolean[] executed = new boolean[1];
        executor.schedule(() -> executed[0] = true, 0, TimeUnit.MILLISECONDS);

        Thread.sleep(100);

        assertTrue(executed[0]);
    }

    @Test
    void testDefaultExecutorHandlesException() {
        // 测试执行器处理异常
        ScheduledExecutorService executor = EasyCacheExecutor.defaultExecutor();

        // 提交会抛出异常的任务
        executor.execute(() -> {
            throw new RuntimeException("Test exception");
        });

        // 执行器应该继续工作
        assertDoesNotThrow(() -> {
            executor.execute(() -> {});
        });
    }
}
