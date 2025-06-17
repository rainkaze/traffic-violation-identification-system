package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.StatisticsDataDto;
import edu.cupk.trafficviolationidentificationsystem.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<StatisticsDataDto> getStatistics() {
        // 不再使用硬编码的模拟数据，而是调用服务层从数据库动态获取
        StatisticsDataDto statistics = statisticsService.getDashboardStatistics();
        return ResponseEntity.ok(statistics);
    }
}