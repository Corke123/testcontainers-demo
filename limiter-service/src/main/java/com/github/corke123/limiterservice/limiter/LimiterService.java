package com.github.corke123.limiterservice.limiter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class LimiterService {

    private static final String KEY_PREFIX = "rate_limit:";
    private final StringRedisTemplate redisTemplate;
    private final LimiterProperties limiterProperties;

    public LimiterService(StringRedisTemplate redisTemplate, LimiterProperties limiterProperties) {
        this.redisTemplate = redisTemplate;
        this.limiterProperties = limiterProperties;
    }

    public boolean checkAndIncrement(String ipAddress) {
        String key = KEY_PREFIX + ipAddress;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count <= limiterProperties.maxAttempts()) {
            redisTemplate.expire(key, Duration.ofSeconds(limiterProperties.windowDurationSeconds()));
        }

        return count != null && count <= limiterProperties.maxAttempts();
    }

}
