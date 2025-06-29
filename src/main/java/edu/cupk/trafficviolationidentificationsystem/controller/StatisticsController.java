package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.service.LeaderboardService;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Set;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final LeaderboardService leaderboardService;

    public StatisticsController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<Set<ZSetOperations.TypedTuple<Object>>> getLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getTopUsers(10)); // 获取前10名
    }
}