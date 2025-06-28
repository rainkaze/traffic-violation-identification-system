// 文件路径: src/main/java/edu/cupk/trafficviolationidentificationsystem/dto/DashboardDataDto.java
package edu.cupk.trafficviolationidentificationsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardDataDto {
    private Stats stats;
    private ChartData violationTypeDistribution;
    private List<RealtimeWarning> realtimeWarnings;
    private List<RecentViolation> recentViolations;

    @Data
    public static class Stats {
        private long totalToday;
        private long processedToday;
        private long pendingToday;
        private long seriousToday;

        private Double totalChange;
        private Double processedChange;
        private Double pendingChange;
        private Double seriousChange;
    }

    @Data
    public static class ChartData {
        private List<String> labels;
        private List<Long> data;
    }

    @Data
    public static class RealtimeWarning {
        private String plateNumber;
        private String violationType;
        private String location;
        private String timeAgo;
        private int warningLevel; // 1 for high, 2 for low

        @JsonIgnore // 该字段仅用于后端计算，不返回给前端
        private String time;
    }

    @Data
    public static class RecentViolation {
        private String time;
        private String plateNumber;
        private String violationType;
        private String location;
        private String status;
    }
}