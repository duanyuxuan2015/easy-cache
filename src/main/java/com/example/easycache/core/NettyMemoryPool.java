package com.example.easycache.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

/**
 * Netty内存池
 * <p>基于Netty的堆外内存池管理</p>
 */
public class NettyMemoryPool {
    /** Netty池化字节缓冲区分配器 */
    public static final PooledByteBufAllocator ALLOCATOR = PooledByteBufAllocator.DEFAULT;

    /**
     * 分配堆外缓冲区
     *
     * @param size 缓冲区大小
     * @return 池化 DirectBuffer
     */
    public static ByteBuf allocateDirectBuffer(int size) {
        return ALLOCATOR.directBuffer(size);
    }

    /**
     * 释放缓冲区（归还到内存池）
     * @param buf 待释放的缓冲区
     */
    public static void releaseBuffer(ByteBuf buf) {
        if (buf != null && buf.refCnt() > 0) {
            buf.release();
        }
    }
}
