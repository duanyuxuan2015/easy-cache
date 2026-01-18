package com.example.easycache.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NettyMemoryPool 单元测试
 * <p>测试Netty内存池的分配和释放功能</p>
 */
public class NettyMemoryPoolTest {

    @Test
    void testAllocateDirectBuffer() {
        // 测试分配堆外缓冲区
        int size = 1024;
        ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(size);

        assertNotNull(buffer);
        assertTrue(buffer.isDirect());
        assertEquals(size, buffer.capacity());

        // 释放缓冲区
        NettyMemoryPool.releaseBuffer(buffer);
    }

    @Test
    void testAllocateSmallBuffer() {
        // 测试分配小缓冲区
        int size = 100;
        ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(size);

        assertNotNull(buffer);
        assertEquals(size, buffer.capacity());

        NettyMemoryPool.releaseBuffer(buffer);
    }

    @Test
    void testAllocateLargeBuffer() {
        // 测试分配大缓冲区
        int size = 1024 * 1024; // 1MB
        ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(size);

        assertNotNull(buffer);
        assertEquals(size, buffer.capacity());

        NettyMemoryPool.releaseBuffer(buffer);
    }

    @Test
    void testWriteAndReadBuffer() {
        // 测试写入和读取缓冲区
        int size = 1024;
        ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(size);

        // 写入数据
        String testData = "Hello, Netty Memory Pool!";
        buffer.writeBytes(testData.getBytes());

        // 读取数据
        byte[] readBytes = new byte[testData.getBytes().length];
        buffer.readBytes(readBytes);
        String result = new String(readBytes);

        assertEquals(testData, result);

        NettyMemoryPool.releaseBuffer(buffer);
    }

    @Test
    void testReleaseNullBuffer() {
        // 测试释放null缓冲区（不应该抛出异常）
        assertDoesNotThrow(() -> {
            NettyMemoryPool.releaseBuffer(null);
        });
    }

    @Test
    void testReleaseBufferMultipleTimes() {
        // 测试多次释放缓冲区
        ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(1024);

        // 第一次释放
        NettyMemoryPool.releaseBuffer(buffer);

        // 第二次释放（应该不会报错）
        assertDoesNotThrow(() -> {
            NettyMemoryPool.releaseBuffer(buffer);
        });
    }

    @Test
    void testBufferRefCnt() {
        // 测试缓冲区引用计数
        ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(1024);

        assertEquals(1, buffer.refCnt());

        NettyMemoryPool.releaseBuffer(buffer);

        assertEquals(0, buffer.refCnt());
    }

    @Test
    void testAllocatorIsPooled() {
        // 测试分配器是池化的
        PooledByteBufAllocator allocator = NettyMemoryPool.ALLOCATOR;

        assertNotNull(allocator);
        assertTrue(allocator.isDirectBufferPooled());
    }

    @Test
    void testAllocateAndReleaseMultipleBuffers() {
        // 测试分配和释放多个缓冲区
        ByteBuf[] buffers = new ByteBuf[10];

        // 分配多个缓冲区
        for (int i = 0; i < buffers.length; i++) {
            buffers[i] = NettyMemoryPool.allocateDirectBuffer(1024);
            assertNotNull(buffers[i]);
        }

        // 释放所有缓冲区
        for (ByteBuf buffer : buffers) {
            NettyMemoryPool.releaseBuffer(buffer);
        }

        // 验证所有缓冲区都已释放
        for (ByteBuf buffer : buffers) {
            assertEquals(0, buffer.refCnt());
        }
    }

    @Test
    void testBufferDataIntegrity() {
        // 测试缓冲区数据完整性
        int size = 2048;
        ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(size);

        // 写入测试数据
        for (int i = 0; i < size; i++) {
            buffer.writeByte(i % 256);
        }

        // 验证数据
        buffer.readerIndex(0);
        for (int i = 0; i < size; i++) {
            byte expected = (byte) (i % 256);
            byte actual = buffer.readByte();
            assertEquals(expected, actual);
        }

        NettyMemoryPool.releaseBuffer(buffer);
    }

    @Test
    void testBufferCapacity() {
        // 测试缓冲区容量
        int[] sizes = {100, 1024, 4096, 10240};

        for (int size : sizes) {
            ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(size);
            assertEquals(size, buffer.capacity());
            NettyMemoryPool.releaseBuffer(buffer);
        }
    }

    @Test
    void testBufferClear() {
        // 测试清空缓冲区
        ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(1024);

        // 写入数据
        buffer.writeBytes("test data".getBytes());

        // 清空缓冲区
        buffer.clear();

        assertEquals(0, buffer.readerIndex());
        assertEquals(0, buffer.writerIndex());
        assertEquals(1024, buffer.capacity());

        NettyMemoryPool.releaseBuffer(buffer);
    }

    @Test
    void testZeroSizeBuffer() {
        // 测试零大小缓冲区
        ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(0);

        assertNotNull(buffer);
        assertEquals(0, buffer.capacity());

        NettyMemoryPool.releaseBuffer(buffer);
    }

    @Test
    void testBufferSlice() {
        // 测试缓冲区分片
        ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(1024);
        buffer.writeBytes("Hello, World!".getBytes());

        ByteBuf slice = buffer.slice();
        assertEquals(buffer.readableBytes(), slice.readableBytes());

        NettyMemoryPool.releaseBuffer(buffer);
        // 不需要释放slice，因为它与buffer共享内存
    }

    @Test
    void testBufferDuplicate() {
        // 测试缓冲区副本
        ByteBuf buffer = NettyMemoryPool.allocateDirectBuffer(1024);
        buffer.writeBytes("Test Data".getBytes());

        ByteBuf duplicate = buffer.duplicate();
        assertEquals(buffer.readableBytes(), duplicate.readableBytes());

        NettyMemoryPool.releaseBuffer(buffer);
        // 不需要释放duplicate，因为它与buffer共享内存
    }
}
