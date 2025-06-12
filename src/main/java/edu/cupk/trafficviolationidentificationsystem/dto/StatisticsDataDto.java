package edu.cupk.trafficviolationidentificationsystem.dto;

import java.util.List;

public class StatisticsDataDto {
    private ChartDataDto violationTrend;
    private ChartDataDto peakTimeAnalysis;
    private ChartDataDto violationTypeDistribution;
    private ChartDataDto regionDistribution;
    private List<TopLocationDto> topLocations;

    // Constructors, Getters, and Setters
    public StatisticsDataDto(ChartDataDto violationTrend, ChartDataDto peakTimeAnalysis, ChartDataDto violationTypeDistribution, ChartDataDto regionDistribution, List<TopLocationDto> topLocations) {
        this.violationTrend = violationTrend;
        this.peakTimeAnalysis = peakTimeAnalysis;
        this.violationTypeDistribution = violationTypeDistribution;
        this.regionDistribution = regionDistribution;
        this.topLocations = topLocations;
    }

    // Getters and Setters...
    public ChartDataDto getViolationTrend() { return violationTrend; }
    public void setViolationTrend(ChartDataDto violationTrend) { this.violationTrend = violationTrend; }
    public ChartDataDto getPeakTimeAnalysis() { return peakTimeAnalysis; }
    public void setPeakTimeAnalysis(ChartDataDto peakTimeAnalysis) { this.peakTimeAnalysis = peakTimeAnalysis; }
    public ChartDataDto getViolationTypeDistribution() { return violationTypeDistribution; }
    public void setViolationTypeDistribution(ChartDataDto violationTypeDistribution) { this.violationTypeDistribution = violationTypeDistribution; }
    public ChartDataDto getRegionDistribution() { return regionDistribution; }
    public void setRegionDistribution(ChartDataDto regionDistribution) { this.regionDistribution = regionDistribution; }
    public List<TopLocationDto> getTopLocations() { return topLocations; }
    public void setTopLocations(List<TopLocationDto> topLocations) { this.topLocations = topLocations; }
}