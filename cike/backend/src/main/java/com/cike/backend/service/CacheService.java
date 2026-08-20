package com.cike.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 缓存前缀
    public static final String PREFIX_NOTE = "note:";
    public static final String PREFIX_USER = "user:";
    public static final String PREFIX_TOPIC = "topic:";
    public static final String PREFIX_HOT_NOTES = "hot:notes";

    // 默认过期时间（秒）
    private static final long DEFAULT_EXPIRE = 3600;

    /**
     * 设置缓存
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, DEFAULT_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 设置缓存（自定义过期时间）
     */
    public void set(String key, Object value, long expireSeconds) {
        redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 判断缓存是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置过期时间
     */
    public void expire(String key, long expireSeconds) {
        redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 递增
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
}
