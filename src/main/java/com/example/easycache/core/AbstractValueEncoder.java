package com.example.easycache.core;

import java.util.function.Function;

/**
 * 抽象值编码器
 * <p>将对象编码为字节数组的基类</p>
 */
public abstract class AbstractValueEncoder implements Function<Object, byte[]> {

    public AbstractValueEncoder() {

    }

}
