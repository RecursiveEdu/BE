/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * @author PrantikGuha
 * CreatedAt: {06-11-2025}
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.username}")
    private String username;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.ssl:true}")
    private boolean sslEnabled;

    @Value("${spring.redis.jedis.pool.max-active:5}")
    private int maxActive;

    @Value("${spring.redis.jedis.pool.max-idle:2}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle:1}")
    private int minIdle;

    @Value("${spring.redis.jedis.pool.max-wait:8500}")
    private long maxWait;

    @Value("${spring.redis.timeout:5000}")
    private long timeout;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        // Basic Redis configuration
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setUsername(username);
        config.setPassword(password);

        // Jedis pool configuration from properties
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWait(Duration.ofMillis(maxWait));

        // Client configuration with SSL and timeout
        JedisClientConfiguration.JedisClientConfigurationBuilder clientConfig = JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofMillis(timeout))
                .readTimeout(Duration.ofMillis(timeout));

        if (sslEnabled) {
            clientConfig.useSsl();
        }

        return new JedisConnectionFactory(config, clientConfig.build());
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(final RedisConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setEnableTransactionSupport(true);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
