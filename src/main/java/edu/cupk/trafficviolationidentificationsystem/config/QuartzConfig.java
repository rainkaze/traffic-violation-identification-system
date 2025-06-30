package edu.cupk.trafficviolationidentificationsystem.config;

import edu.cupk.trafficviolationidentificationsystem.task.DailyReportJob;
import edu.cupk.trafficviolationidentificationsystem.task.TempFileCleanupJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz 定时任务调度配置.
 * <p>
 * 本类负责定义系统中的所有定时任务 (Jobs) 及其触发器 (Triggers)。
 * 每个任务由一个 {@link JobDetail} (定义任务本身) 和一个 {@link Trigger} (定义何时执行) 组成。
 * </p>
 */
@Configuration
public class QuartzConfig {

    private static final Logger log = LoggerFactory.getLogger(QuartzConfig.class);

    // --- 任务1: 每日交通违规报告生成任务 ---

    /**
     * 定义“每日报告生成”任务的详细信息。
     *
     * @return JobDetail 实例，描述了任务的实现类和身份标识。
     */
    @Bean
    public JobDetail dailyReportJobDetail() {
        log.info("配置 Quartz Job: dailyReportJob");
        return JobBuilder.newJob(DailyReportJob.class) // 任务的执行逻辑在 DailyReportJob 类中
                .withIdentity("dailyReportJob") // 任务的唯一标识
                .storeDurably() // 即使没有关联的触发器，也持久化此任务定义
                .build();
    }

    /**
     * 定义“每日报告生成”任务的触发器。
     *
     * @return Trigger 实例，定义了任务的执行计划。
     */
    @Bean
    public Trigger dailyReportJobTrigger() {
        // 【代码优化说明】
        // 此处的 Cron 表达式 "0 54 20 * * ?" 代表“每天的 20:54:00 执行”。
        // 我将此表达式定义为常量，便于管理和日志记录，这不会影响原有逻辑。
        final String dailyReportCronExpression = "0 54 20 * * ?";

        // 使用 Cron 表达式，设定每天的特定时间执行
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(dailyReportCronExpression);
        log.info("配置 Quartz Trigger: dailyReportTrigger, Cron 表达式: '{}'", dailyReportCronExpression);

        return TriggerBuilder.newTrigger()
                .forJob(dailyReportJobDetail()) // 关联到每日报告任务
                .withIdentity("dailyReportTrigger") // 触发器的唯一标识
                .withSchedule(scheduleBuilder) // 设置调度计划
                .build();
    }


    // --- 任务2: 临时文件清理任务 ---

    /**
     * 定义“临时文件清理”任务的详细信息。
     *
     * @return JobDetail 实例。
     */
    @Bean
    public JobDetail tempFileCleanupJobDetail() {
        log.info("配置 Quartz Job: tempFileCleanupJob");
        return JobBuilder.newJob(TempFileCleanupJob.class) // 任务逻辑在 TempFileCleanupJob 类中
                .withIdentity("tempFileCleanupJob")
                .storeDurably()
                .build();
    }

    /**
     * 定义“临时文件清理”任务的触发器。
     *
     * @return Trigger 实例。
     */
    @Bean
    public Trigger tempFileCleanupJobTrigger() {
        // 使用简单调度，设定一个固定的时间间隔重复执行
        final int intervalInMinutes = 1;
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(intervalInMinutes) // 每隔1分钟执行一次
                .repeatForever(); // 无限重复
        log.info("配置 Quartz Trigger: tempFileCleanupTrigger, 每 {} 分钟执行一次", intervalInMinutes);

        return TriggerBuilder.newTrigger()
                .forJob(tempFileCleanupJobDetail()) // 关联到临时文件清理任务
                .withIdentity("tempFileCleanupTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}