// 文件路径: src/main/java/edu/cupk/trafficviolationidentificationsystem/service/DashboardService.java
package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.DashboardDataDto;

public interface DashboardService {
    DashboardDataDto getDashboardData(String timeRange);
}