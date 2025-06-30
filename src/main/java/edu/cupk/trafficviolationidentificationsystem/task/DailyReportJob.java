package edu.cupk.trafficviolationidentificationsystem.task;

import edu.cupk.trafficviolationidentificationsystem.service.ViolationProcessingService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * 每日报告生成 Quartz Job.
 * <p>
 * 这是一个由 Quartz 调度的作业，其唯一职责是触发 {@link ViolationProcessingService}
 * 中生成每日违规报告的逻辑。
 * 该 Job 的调度由 {@link edu.cupk.trafficviolationidentificationsystem.config.QuartzConfig} 配置。
 * </p>
 */
@Component
public class DailyReportJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(DailyReportJob.class);

    @Autowired
    private ViolationProcessingService violationProcessingService;

    /**
     * Quartz 调度器执行此方法。
     * @param context 包含作业运行时信息的上下文。
     */
    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("Quartz Job 'DailyReportJob' 开始执行...");
        try {
            violationProcessingService.generateDailyViolationReport();
            log.info("Quartz Job 'DailyReportJob' 执行成功。");
        } catch (Exception e) {
            log.error("执行 'DailyReportJob' 时发生错误。", e);
        }
    }
}