package edu.cupk.trafficviolationidentificationsystem.task;

import edu.cupk.trafficviolationidentificationsystem.service.ViolationProcessingService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * 临时文件清理 Quartz Job.
 * <p>
 * 这是一个由 Quartz 调度的作业，负责调用服务层逻辑以清理系统生成的临时文件。
 * 该 Job 的调度由 {@link edu.cupk.trafficviolationidentificationsystem.config.QuartzConfig} 配置。
 * </p>
 */
@Component
public class TempFileCleanupJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(TempFileCleanupJob.class);

    @Autowired
    private ViolationProcessingService violationProcessingService;

    /**
     * Quartz 调度器执行此方法。
     * @param context 包含作业运行时信息的上下文。
     */
    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("Quartz Job 'TempFileCleanupJob' 开始执行...");
        try {
            violationProcessingService.cleanupTemporaryFiles();
            log.info("Quartz Job 'TempFileCleanupJob' 执行成功。");
        } catch (Exception e) {
            log.error("执行 'TempFileCleanupJob' 时发生错误。", e);
        }
    }
}