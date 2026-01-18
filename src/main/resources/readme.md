项目概述
这是一个基于Spring Boot的分布式缓存框架，名为 "easy-cache-spring-boot-starter"。它提供了一个多级缓存解决方案，支持本地缓存和远程Redis缓存，并具有缓存同步、自动加载等功能。
核心特性
多级缓存架构：
支持本地缓存（Caffeine、OHC）
支持远程Redis缓存
可配置缓存类型：仅本地、仅远程、双层缓存
多种缓存实现：
CaffeineCache：基于Caffeine的内存缓存
OHCCache：基于OHC（Off-Heap Cache）的堆外缓存
CaffeineOffHeapCache：使用Netty内存池的堆外缓存实现
RedisCache：基于Redis的远程缓存
缓存同步机制：
通过Redis Stream实现缓存变更广播
多实例间缓存一致性保障
灵活的配置：
支持过期时间设置（全局/本地）
支持缓存容量限制
支持空值缓存防穿透
支持自定义序列化器
自动加载机制：
支持缓存未命中时自动加载数据
提供加载锁防止重复加载
核心组件
Cache接口：定义了基本的缓存操作方法(get/getAll/put/putAll/remove/removeAll)
AbstractCache抽象类：实现了Cache接口的基本逻辑，包括键转换等通用功能
MultiLevelCache：多级缓存的核心实现，协调本地和远程缓存的操作
CacheManager：缓存管理器，负责创建和获取缓存实例
BroadcastManager：广播管理器，负责在多个缓存实例间同步缓存状态
CacheConfig/CacheConfigBuilder：缓存配置及构建器，提供了链式调用的配置方式
技术栈
Spring Boot 3.2.12
Redis (作为远程缓存)
Caffeine (作为本地内存缓存)
OHC (Off-Heap Cache，堆外缓存)
Kryo5 (默认序列化方案)
Fastjson2 (JSON序列化)
Netty (内存管理)
设计亮点
分层设计：清晰的接口与实现分离，易于扩展新的缓存实现
自动配置：基于Spring Boot Starter模式，可直接集成到Spring Boot应用中
缓存穿透防护：支持空值缓存，防止恶意请求穿透缓存层
缓存雪崩防护：支持加载锁机制，防止大量并发请求同时访问数据库
多级缓存同步：通过Redis Stream实现分布式环境下的缓存同步
总结
该项目是一个功能完整的分布式缓存解决方案，特别适合需要高性能、多级缓存架构的企业级应用。它结合了本地缓存的高速访问和远程缓存的数据共享优势，同时解决了分布式环境下的一致性问题。通过合理的配置，可以显著提升系统的响应速度和并发处理能力。