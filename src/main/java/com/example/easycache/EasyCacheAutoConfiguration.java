package com.example.easycache;

import com.alibaba.fastjson2.JSON;
import com.example.easycache.core.BroadcastManager;
import com.example.easycache.core.CacheManager;
import com.example.easycache.core.EasyCacheProperties;
import com.example.easycache.core.SimpleCacheManager;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存自动配置类
 * <p>Spring Boot自动配置，初始化缓存相关Bean</p>
 */
@Configuration
public class EasyCacheAutoConfiguration {

    /**
     * 创建Redis配置属性
     *
     * @return Redis配置属性
     */
    @Bean(name = "easyCacheRedisProperties")
    @ConfigurationProperties("easy-cache.redis")
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    /**
     * 创建缓存配置属性
     *
     * @return 缓存配置属性
     */
    @Bean
    @ConfigurationProperties(prefix = "easy-cache")
    public EasyCacheProperties easyCacheProperties() {
        return new EasyCacheProperties();
    }

    /**
     * 创建Redis连接工厂
     *
     * @param properties Redis配置属性
     * @return Redis连接工厂
     */
    private LettuceConnectionFactory createConnectionFactory(RedisProperties properties) {
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.shutdownTimeout(properties.getLettuce().getShutdownTimeout());
        builder.commandTimeout(redisProperties().getTimeout());
        GenericObjectPoolConfig genericObjectPoolConfig = buildGenericObjectPoolConfig(properties);
        if (genericObjectPoolConfig != null) {
            builder.poolConfig(genericObjectPoolConfig);
        }
        //sentinel模式
        if (properties.getSentinel() != null) {
            RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration();
            sentinelConfiguration.setSentinels(convert(properties.getSentinel().getNodes()));
            sentinelConfiguration.setSentinelUsername(properties.getUsername());
            sentinelConfiguration.setPassword(properties.getPassword());
            sentinelConfiguration.setMaster(properties.getSentinel().getMaster());
            return new LettuceConnectionFactory(sentinelConfiguration,builder.build());
        }
        if(properties.getCluster() !=null){
            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
            redisClusterConfiguration.setUsername(properties.getUsername());
            redisClusterConfiguration.setPassword(properties.getPassword());
            redisClusterConfiguration.setMaxRedirects(properties.getCluster().getMaxRedirects());
            redisClusterConfiguration.setClusterNodes(convert(properties.getCluster().getNodes()));
            return new LettuceConnectionFactory(redisClusterConfiguration,builder.build());
        }

        // 单机模式配置
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.getHost());
        config.setPort(properties.getPort());
        config.setPassword(properties.getPassword());
        config.setUsername(properties.getUsername());
        return new LettuceConnectionFactory(config, builder.build());
    }

    /**
     * 构建通用对象池配置
     *
     * @param redisProperties Redis配置属性
     * @return 对象池配置
     */
    private GenericObjectPoolConfig buildGenericObjectPoolConfig(RedisProperties redisProperties) {
        RedisProperties.Pool pool = redisProperties().getLettuce().getPool();
        if (pool == null) return null;
        GenericObjectPoolConfig genericObjectPoolConfig =
                new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(pool.getMaxIdle());
        genericObjectPoolConfig.setMinIdle(pool.getMinIdle());
        genericObjectPoolConfig.setMaxTotal(pool.getMaxActive());
        genericObjectPoolConfig.setMaxWait(pool.getMaxWait());
        return genericObjectPoolConfig;
    }

    /**
     * 转换节点字符串为Redis节点列表
     *
     * @param nodeStrings 节点字符串列表
     * @return Redis节点列表
     */
    private static List<RedisNode> convert(List<String> nodeStrings) {
        List<RedisNode> redisNodes = new ArrayList<>();
        if (nodeStrings == null || nodeStrings.isEmpty()) {
            return redisNodes;
        }
        for (String nodeStr : nodeStrings) {
            String[] parts = nodeStr.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("无效的节点格式:" + nodeStr + "，正确格式应为 host:port");
            }
            String host = parts[0].trim();
            int port;
            try {
                port = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("节点端口不是有效数字:" + parts[1], e);
            }
            RedisNode redisNode = new RedisNode(host, port);
            redisNodes.add(redisNode);
        }
        return redisNodes;
    }

    /**
     * 创建缓存Redis模板
     *
     * @return Redis模板
     */
    @Bean(value="easyCacheRedisTemplate")
    public RedisTemplate<String,byte[]> cacheRedisTemplate(){
        //创建客户端连接
        LettuceConnectionFactory lettuceConnectionFactory =
                createConnectionFactory(redisProperties());
        lettuceConnectionFactory.afterPropertiesSet();
        RedisTemplate<String,byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 创建缓存管理器
     *
     * @return 缓存管理器
     */
    @Bean
    CacheManager cacheManager(){
        RedisTemplate<String,byte[]> redisTemplate = cacheRedisTemplate();
        CacheManager cacheManager = new  SimpleCacheManager(redisTemplate);
        BroadcastManager broadcastManager = new BroadcastManager(cacheManager,easyCacheProperties(),redisTemplate);
        cacheManager.setBroadcastManager(broadcastManager);
        broadcastManager.startSubscribe();
        return cacheManager;
    }

}
