package edu.cupk.trafficviolationidentificationsystem.task;

import edu.cupk.trafficviolationidentificationsystem.service.ViolationProcessingService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class DailyReportJob extends QuartzJobBean {

    @Autowired
    private ViolationProcessingService violationProcessingService;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        violationProcessingService.generateDailyViolationReport();
    }
}