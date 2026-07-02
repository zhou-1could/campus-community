package com.campus.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {
    private final StringRedisTemplate redisTemplate;
    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_EXPIRE = 10;

    // Lua script for atomic unlock: only delete if value matches
    private static final String UNLOCK_LUA =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

    private final ThreadLocal<String> lockValue = new ThreadLocal<>();

    public RedisLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryLock(String lockName, long expireSeconds) {
        String value = UUID.randomUUID().toString();
        lockValue.set(value);
        String key = LOCK_PREFIX + lockName;
        Boolean ok = redisTemplate.opsForValue()
                .setIfAbsent(key, value, expireSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(ok);
    }

    public boolean tryLock(String lockName) {
        return tryLock(lockName, DEFAULT_EXPIRE);
    }

    public void unlock(String lockName) {
        String key = LOCK_PREFIX + lockName;
        String value = lockValue.get();
        if (value == null) return;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_LUA, Long.class);
        redisTemplate.execute(script, Collections.singletonList(key), value);
        lockValue.remove();
    }
}
