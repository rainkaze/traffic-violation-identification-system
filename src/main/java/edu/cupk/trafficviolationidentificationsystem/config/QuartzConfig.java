package edu.cupk.trafficviolationidentificationsystem.config;

import edu.cupk.trafficviolationidentificationsystem.task.DailyReportJob;
import edu.cupk.trafficviolationidentificationsystem.task.TempFileCleanupJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    // --- 配置每日报告任务 ---
    @Bean
    public JobDetail dailyReportJobDetail() {
        return JobBuilder.newJob(DailyReportJob.class)
                .withIdentity("dailyReportJob")
                .storeDurably() // 即使没有Trigger关联，也保留Job
                .build();
    }

    @Bean
    public Trigger dailyReportJobTrigger() {
        // 使用Cron表达式，设定每天凌晨0点1分执行
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 54 20 * * ?");

        return TriggerBuilder.newTrigger()
                .forJob(dailyReportJobDetail())
                .withIdentity("dailyReportTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }


    // --- 配置临时文件清理任务 ---
    @Bean
    public JobDetail tempFileCleanupJobDetail() {
        return JobBuilder.newJob(TempFileCleanupJob.class)
                .withIdentity("tempFileCleanupJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger tempFileCleanupJobTrigger() {
        // 使用SimpleSchedule，为了方便演示，设定为每2分钟执行一次
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes(1) // 每2分钟执行一次
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(tempFileCleanupJobDetail())
                .withIdentity("tempFileCleanupTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}