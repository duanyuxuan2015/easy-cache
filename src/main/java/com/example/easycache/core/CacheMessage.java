package com.example.easycache.core;

import java.io.Serializable;

/**
 * 缓存消息
 * <p>用于集群间缓存同步的消息对象</p>
 */
public class CacheMessage implements Serializable {
    private static final long serialVersionUID = -462475561129953207L;

    /** 添加类型 */
    public static final int TYPE_PUT = 1;
    /** 批量添加类型 */
    public static final int TYPE_PUT_ALL = 2;
    /** 移除类型 */
    public static final int TYPE_REMOVE = 3;
    /** 批量移除类型 */
    public static final int TYPE_REMOVE_ALL = 4;

    /** 消息源ID */
    private String sourceId;

    /** 缓存名称 */
    private String cacheName;

    /** 消息类型 */
    private int type;

    /** 键数组 */
    private String[] keys;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
