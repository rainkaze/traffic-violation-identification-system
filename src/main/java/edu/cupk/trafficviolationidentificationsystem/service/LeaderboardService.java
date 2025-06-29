package edu.cupk.trafficviolationidentificationsystem.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class LeaderboardService {

    private static final String LEADERBOARD_KEY = "leaderboard:processed_violations";
    private final RedisTemplate<String, Object> redisTemplate;

    public LeaderboardService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void incrementScore(String member, double score) {
        redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, member, score);
    }

    public Set<ZSetOperations.TypedTuple<Object>> getTopUsers(long count) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(LEADERBOARD_KEY, 0, count - 1);
    }
}