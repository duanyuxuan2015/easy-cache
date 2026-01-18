package com.example.easycache.core;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 缓存执行器
 * <p>提供默认的定时任务线程池</p>
 */
public class EasyCacheExecutor {
    /** 默认执行器 */
    protected volatile static ScheduledExecutorService defaultExecutor;
    /** 可重入锁 */
    private static final ReentrantLock reentrantLock = new ReentrantLock();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (defaultExecutor != null) {
                    defaultExecutor.shutdownNow();
                }
            }
        });
    }

    /**
     * 获取默认执行器
     * <p>使用双重检查锁定确保单例</p>
     *
     * @return 定时任务执行器
     */
    public static ScheduledExecutorService defaultExecutor() {
        if (defaultExecutor != null) {
            return defaultExecutor;
        }
        reentrantLock.lock();
        try{
            if (defaultExecutor == null) {
                ThreadFactory tf = r -> {
                    Thread t = new Thread(r, "EasyCacheDefaultExecutor");
                    t.setDaemon(true);

                    ClassLoader classLoader = EasyCacheExecutor.class.getClassLoader();
                    if (classLoader == null) {
                        // This class was loaded by the Bootstrap ClassLoader,
                        // so let's tie the thread's context ClassLoader to the System ClassLoader instead.
                        classLoader = ClassLoader.getSystemClassLoader();
                    }
                    t.setContextClassLoader(classLoader);

                    return t;
                };
                int coreSize = Math.min(4, Runtime.getRuntime().availableProcessors());
                defaultExecutor = new ScheduledThreadPoolExecutor(coreSize, tf);
            }
        }finally {
            reentrantLock.unlock();
        }
        return defaultExecutor;
    }
}
