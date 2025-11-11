/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author PrantikGuha
 * CreatedAt: {06-11-2025}
 */
@Slf4j
public class RedisHelper {

    public static void cache(RedisTemplate<String, String> redisTemplate, String redisKey,
                             String value, long ttl, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(redisKey, value, ttl, timeUnit);
        } catch (Exception exception) {
            log.error("Exception in RedisHelper.cache, while caching: {} to Redis with key: {} ",
                    value, redisKey, exception);
        }
    }

    public static String get(RedisTemplate<String, String> redisTemplate, String redisKey) {
        try {
            return redisTemplate.opsForValue().get(redisKey);
        } catch (Exception exception) {
            log.error("Exception in RedisHelper.get, while fetching data with key: {} ", redisKey, exception);
        }
        return null;
    }

    public static void increment(RedisTemplate<String, String> redisTemplate, String redisKey) {
        try {
            redisTemplate.opsForValue().increment(redisKey);
        } catch (Exception exception) {
            log.error("Exception in RedisHelper.get, while incrementing with key: {} ", redisKey, exception);
        }
    }

    public static void delete(RedisTemplate<String, String> redisTemplate, String... redisKeys) {
        try {

            redisTemplate.delete(Arrays.asList(redisKeys));
        } catch (Exception exception) {
            log.error("Exception in RedisHelper.get, while deleting with keys: {} ", redisKeys, exception);
        }
    }

    public static String getKey(String format, String... parameters) {
        return String.format(format, parameters);
    }
}
