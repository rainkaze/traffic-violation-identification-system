// 文件路径: src/main/java/edu/cupk/trafficviolationidentificationsystem/repository/DashboardMapper.java
package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.dto.CountByLabelDto;
import edu.cupk.trafficviolationidentificationsystem.dto.DashboardDataDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DashboardMapper {
    long countTotalToday();
    long countProcessedToday();
    long countPendingToday();
    long countSeriousToday(); // 扣分 > 6
    long countTotalYesterday();
    long countProcessedYesterday();
    long countPendingYesterday();
    long countSeriousYesterday();
    List<CountByLabelDto> getViolationTypeDistribution(@Param("timeRange") String timeRange);
    List<DashboardDataDto.RealtimeWarning> getRealtimeWarnings();
    List<DashboardDataDto.RecentViolation> getRecentViolations();
}