package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.ChartDataDto;
import edu.cupk.trafficviolationidentificationsystem.dto.CountByLabelDto;
import edu.cupk.trafficviolationidentificationsystem.dto.StatisticsDataDto;
import edu.cupk.trafficviolationidentificationsystem.dto.TopLocationDto;
import edu.cupk.trafficviolationidentificationsystem.repository.StatisticsMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

public interface StatisticsService {
    StatisticsDataDto getDashboardStatistics();
}

@Service
class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    public StatisticsServiceImpl(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    @Override
    public StatisticsDataDto getDashboardStatistics() {
        // 1. 违法趋势数据 (过去30天)
        ChartDataDto violationTrend = convertToChartData(statisticsMapper.countByDayLast30Days());

        // 2. 高峰时段分析数据
        ChartDataDto peakTimeAnalysis = convertToChartData(statisticsMapper.countByHour());

        // 3. 违法类型分布数据
        ChartDataDto violationTypeDistribution = convertToChartData(statisticsMapper.countByViolationType());

        // 4. 区域违法分布数据
        ChartDataDto regionDistribution = convertToChartData(statisticsMapper.countByDistrict());

        // 5. 违法高发地点 TOP 5
        List<TopLocationDto> topLocations = statisticsMapper.findTop5Locations();
        // 为TopLocationDto填充排名和模拟的趋势数据
        for (int i = 0; i < topLocations.size(); i++) {
            TopLocationDto loc = topLocations.get(i);
            loc.setRank(i + 1);
            // 模拟趋势和主要类型，真实场景需要更复杂的计算
            loc.setTrend((Math.random() - 0.5) * 0.2); // 随机生成 -10% 到 +10% 的趋势
            loc.setPrimaryViolationType("闯红灯"); // 模拟数据
        }

        return new StatisticsDataDto(
                violationTrend,
                peakTimeAnalysis,
                violationTypeDistribution,
                regionDistribution,
                topLocations
        );
    }

    // 辅助方法，将数据库查询结果转换为图表DTO格式
    private ChartDataDto convertToChartData(List<CountByLabelDto> dbResult) {
        List<String> labels = dbResult.stream().map(CountByLabelDto::getLabel).collect(Collectors.toList());
        List<Number> data = dbResult.stream().map(CountByLabelDto::getValue).collect(Collectors.toList());
        return new ChartDataDto(labels, data);
    }
}