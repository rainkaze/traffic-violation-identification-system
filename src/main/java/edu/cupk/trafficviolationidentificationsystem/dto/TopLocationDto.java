package edu.cupk.trafficviolationidentificationsystem.dto;

public class TopLocationDto {
    private int rank;
    private String location;
    private String region;
    private int count;
    private String primaryViolationType;
    private double trend; // e.g., 0.10 for +10%, -0.05 for -5%

    // Constructors, Getters, and Setters
    public TopLocationDto(int rank, String location, String region, int count, String primaryViolationType, double trend) {
        this.rank = rank;
        this.location = location;
        this.region = region;
        this.count = count;
        this.primaryViolationType = primaryViolationType;
        this.trend = trend;
    }

    // Getters and Setters ...
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public String getPrimaryViolationType() { return primaryViolationType; }
    public void setPrimaryViolationType(String primaryViolationType) { this.primaryViolationType = primaryViolationType; }
    public double getTrend() { return trend; }
    public void setTrend(double trend) { this.trend = trend; }
}