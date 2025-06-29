package edu.cupk.trafficviolationidentificationsystem.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CounterService {

    private final RedisTemplate<String, Object> redisTemplate;

    public CounterService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void increment(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    public Integer getCount(String key) {
        return (Integer) redisTemplate.opsForValue().get(key);
    }
}