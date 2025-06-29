package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.model.DailyReport;
import edu.cupk.trafficviolationidentificationsystem.repository.DailyReportRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final DailyReportRepository reportRepository;

    public ReportController(DailyReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyReport>> getAllDailyReports() {
        List<DailyReport> reports = reportRepository.findAllByOrderByReportDateDesc();
        return ResponseEntity.ok(reports);
    }
}