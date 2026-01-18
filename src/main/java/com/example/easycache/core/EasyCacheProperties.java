package com.example.easycache.core;

/**
 * 缓存配置属性类
 * <p>定义缓存的配置参数</p>
 */
public class EasyCacheProperties {

   /** 任务初始化延迟时间（秒） */
   private int taskInitDelay=10;

   /** 任务执行周期（秒） */
   private int taskPeriod=10;

   /** 消费数量 */
   private int consumeCount=10;

   /** 缓存消息大小 */
   private int cacheMessageSize=1000;

   /** 阻塞时长（毫秒） */
   private int blockDuration=2000;

   public int getTaskInitDelay() {
      return taskInitDelay;
   }

   public void setTaskInitDelay(int taskInitDelay) {
      this.taskInitDelay = taskInitDelay;
   }

   public int getTaskPeriod() {
      return taskPeriod;
   }

   public void setTaskPeriod(int taskPeriod) {
      this.taskPeriod = taskPeriod;
   }

   public int getConsumeCount() {
      return consumeCount;
   }

   public void setConsumeCount(int consumeCount) {
      this.consumeCount = consumeCount;
   }

   public int getBlockDuration() {
      return blockDuration;
   }

   public void setBlockDuration(int blockDuration) {
      this.blockDuration = blockDuration;
   }

   public int getCacheMessageSize() {
      return cacheMessageSize;
   }

   public void setCacheMessageSize(int cacheMessageSize) {
      this.cacheMessageSize = cacheMessageSize;
   }
}
