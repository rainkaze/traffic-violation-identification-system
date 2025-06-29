package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.DashboardDataDto;
import edu.cupk.trafficviolationidentificationsystem.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘数据控制器。
 * 提供获取系统概览数据的API接口，用于前端仪表盘展示。
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 构造函数，用于注入仪表盘服务。
     *
     * @param dashboardService 仪表盘服务实例。
     */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 根据指定的时间范围获取仪表盘的聚合数据。
     *
     * @param timeRange 时间范围字符串，例如 "day", "week", "month", "year"。默认为 "month"。
     * @return 包含各项统计数据的DashboardDataDto对象。
     */
    @GetMapping("/data")
    public ResponseEntity<DashboardDataDto> getDashboardData(
            @RequestParam(defaultValue = "month") String timeRange) {
        DashboardDataDto data = dashboardService.getDashboardData(timeRange);
        return ResponseEntity.ok(data);
    }
}