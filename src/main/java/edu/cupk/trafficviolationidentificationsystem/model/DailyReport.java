package edu.cupk.trafficviolationidentificationsystem.model;

// 唯一的修改在这里：从 javax 改为 jakarta
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_reports")
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private LocalDate reportDate;

    private Integer totalViolations;

    private String generatedBy; // 记录由哪个模块生成，这里是 "Quartz"

    // Getters and Setters
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public Integer getTotalViolations() { return totalViolations; }
    public void setTotalViolations(Integer totalViolations) { this.totalViolations = totalViolations; }
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
}