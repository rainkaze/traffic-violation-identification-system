// 文件路径: src/main/java/edu/cupk/trafficviolationidentificationsystem/controller/DashboardController.java
package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.DashboardDataDto;
import edu.cupk.trafficviolationidentificationsystem.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/data")
    public ResponseEntity<DashboardDataDto> getDashboardData(
            @RequestParam(defaultValue = "month") String timeRange) {
        DashboardDataDto data = dashboardService.getDashboardData(timeRange);
        return ResponseEntity.ok(data);
    }
}