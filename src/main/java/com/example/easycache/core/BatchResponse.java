package com.example.easycache.core;

import java.util.Collection;

/**
 * 批量响应对象
 * <p>用于批量获取缓存数据的响应</p>
 */
public class BatchResponse{
    /** 缓存名称 */
    private String cacheName;
    /** 值集合 */
    private Collection values;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public Collection getValues() {
        return values;
    }

    public void setValues(Collection values) {
        this.values = values;
    }
}
