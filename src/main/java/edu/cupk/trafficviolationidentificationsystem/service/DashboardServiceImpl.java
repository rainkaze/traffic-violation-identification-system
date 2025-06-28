// 文件路径: src/main/java/edu/cupk/trafficviolationidentificationsystem/service/DashboardServiceImpl.java
package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.CountByLabelDto;
import edu.cupk.trafficviolationidentificationsystem.dto.DashboardDataDto;
import edu.cupk.trafficviolationidentificationsystem.repository.DashboardMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;

    public DashboardServiceImpl(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public DashboardDataDto getDashboardData(String timeRange) {
        DashboardDataDto data = new DashboardDataDto();
        DashboardDataDto.Stats stats = new DashboardDataDto.Stats();

        // 1. 获取今日统计数据
        long totalToday = dashboardMapper.countTotalToday();
        long processedToday = dashboardMapper.countProcessedToday();
        long pendingToday = dashboardMapper.countPendingToday();
        long seriousToday = dashboardMapper.countSeriousToday();

        stats.setTotalToday(totalToday);
        stats.setProcessedToday(processedToday);
        stats.setPendingToday(pendingToday);
        stats.setSeriousToday(seriousToday);

        // 2. 获取昨日数据用于对比
        long totalYesterday = dashboardMapper.countTotalYesterday();
        long processedYesterday = dashboardMapper.countProcessedYesterday();
        long pendingYesterday = dashboardMapper.countPendingYesterday();
        long seriousYesterday = dashboardMapper.countSeriousYesterday();

        // 3. 计算百分比变化并设置
        stats.setTotalChange(calculatePercentageChange(totalToday, totalYesterday));
        stats.setProcessedChange(calculatePercentageChange(processedToday, processedYesterday));
        stats.setPendingChange(calculatePercentageChange(pendingToday, pendingYesterday));
        stats.setSeriousChange(calculatePercentageChange(seriousToday, seriousYesterday));

        data.setStats(stats);

        // 4. 获取图表数据
        List<CountByLabelDto> distribution = dashboardMapper.getViolationTypeDistribution(timeRange);
        DashboardDataDto.ChartData chartData = new DashboardDataDto.ChartData();
        chartData.setLabels(distribution.stream().map(CountByLabelDto::getLabel).collect(Collectors.toList()));
        chartData.setData(distribution.stream().map(CountByLabelDto::getValue).collect(Collectors.toList()));
        data.setViolationTypeDistribution(chartData);

        // 5. 获取实时预警
        List<DashboardDataDto.RealtimeWarning> warnings = dashboardMapper.getRealtimeWarnings();
        warnings.forEach(w -> {
            w.setTimeAgo(formatTimeAgo(w.getTime()));
            w.setTime(null);
        });
        data.setRealtimeWarnings(warnings);

        // 6. 获取最近违法记录
        data.setRecentViolations(dashboardMapper.getRecentViolations());

        return data;
    }

    /**
     * 计算百分比变化. (已优化)
     * @param today 今日数值
     * @param yesterday 昨日数值
     * @return 百分比
     */
    private Double calculatePercentageChange(long today, long yesterday) {
        if (yesterday == 0) {
            if (today > 0) {
                // 如果昨日为0，今日大于0，定义为100%增长
                return 100.0;
            }
            // 如果昨日和今日都为0，则无变化
            return 0.0;
        }
        // 公式: ((今日 - 昨日) / 昨日) * 100
        return ((double) (today - yesterday) / yesterday) * 100;
    }

    private String formatTimeAgo(String timeStr) {
        if (timeStr == null) return "未知时间";
        try {
            LocalDateTime eventTime = LocalDateTime.parse(timeStr.replace(" ", "T"));
            Duration duration = Duration.between(eventTime, LocalDateTime.now());
            long minutes = duration.toMinutes();
            if (minutes < 1) return "刚刚";
            if (minutes < 60) return minutes + "分钟前";
            long hours = duration.toHours();
            if (hours < 24) return hours + "小时前";
            return duration.toDays() + "天前";
        } catch (DateTimeParseException e) {
            return timeStr;
        }
    }
}