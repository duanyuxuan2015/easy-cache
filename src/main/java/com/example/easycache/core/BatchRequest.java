package com.example.easycache.core;

import java.util.Set;

/**
 * 批量请求对象
 * <p>用于批量获取缓存数据的请求</p>
 */
public class BatchRequest{
    /** 缓存名称 */
    private String cacheName;
    /** 键集合 */
    private Set keys;

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public Set getKeys() {
        return keys;
    }

    public void setKeys(Set keys) {
        this.keys = keys;
    }

}
