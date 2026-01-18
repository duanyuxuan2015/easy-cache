package com.example.easycache.core;

import com.alibaba.fastjson2.JSON;

/**
 * Fastjson2键转换器
 * <p>使用Fastjson2将对象转换为JSON字符串作为缓存键</p>
 *
 * @param <K> 键类型
 */
public class Fastjson2KeyConvertor<K> implements KeyConvertor<K>  {

    /** 单例实例 */
    public static final Fastjson2KeyConvertor INSTANCE = new Fastjson2KeyConvertor();

    /**
     * 转换键
     *
     * @param cacheName 缓存名称
     * @param originalKey 原始键
     * @return 转换后的键
     */
    @Override
    public String apply(String cacheName ,K originalKey) {
        if (originalKey == null) {
            return null;
        }
        if (originalKey instanceof String) {
            return (String) originalKey;
        }
        return JSON.toJSONString(originalKey);
    }

}

