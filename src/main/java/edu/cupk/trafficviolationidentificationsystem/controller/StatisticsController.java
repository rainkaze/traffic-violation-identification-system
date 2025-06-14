package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.ChartDataDto;
import edu.cupk.trafficviolationidentificationsystem.dto.StatisticsDataDto;
import edu.cupk.trafficviolationidentificationsystem.dto.TopLocationDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
// @CrossOrigin 注解已被移除，统一由 SecurityConfig 管理
public class StatisticsController {

    @GetMapping
    public StatisticsDataDto getStatistics() {
        // 1. 违法趋势数据 (模拟)
        ChartDataDto violationTrend = new ChartDataDto(
                Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日"),
                Arrays.asList(120, 135, 130, 142, 160, 185, 170)
        );

        // 2. 高峰时段分析数据 (模拟)
        ChartDataDto peakTimeAnalysis = new ChartDataDto(
                Arrays.asList("07-09", "09-11", "11-13", "13-15", "15-17", "17-19", "19-21"),
                Arrays.asList(45, 28, 22, 25, 38, 55, 30)
        );

        // 3. 违法类型分布数据 (模拟)
        ChartDataDto violationTypeDistribution = new ChartDataDto(
                Arrays.asList("闯红灯", "超速", "逆行", "其他"),
                Arrays.asList(55, 42, 24, 58)
        );

        // 4. 区域违法分布数据 (模拟)
        ChartDataDto regionDistribution = new ChartDataDto(
                Arrays.asList("克拉玛依区", "独山子区", "白碱滩区", "乌尔禾区"),
                Arrays.asList(40, 25, 20, 15)
        );

        // 5. 违法高发地点 TOP 5 (模拟)
        List<TopLocationDto> topLocations = Arrays.asList(
                new TopLocationDto(1, "世纪大道与友谊路口", "克拉玛依区", 58, "闯红灯", 0.10),
                new TopLocationDto(2, "石化大道与南京路口", "独山子区", 45, "不按导向车道行驶", -0.05),
                new TopLocationDto(3, "和平路", "白碱滩区", 32, "逆行, 违停", 0.08),
                new TopLocationDto(4, "G30高速K3550+200", "高速路段", 28, "超速行驶", 0.02),
                new TopLocationDto(5, "迎宾路", "乌尔禾区", 22, "违法变道", -0.03)
        );

        // 组装并返回所有数据
        return new StatisticsDataDto(
                violationTrend,
                peakTimeAnalysis,
                violationTypeDistribution,
                regionDistribution,
                topLocations
        );
    }
}