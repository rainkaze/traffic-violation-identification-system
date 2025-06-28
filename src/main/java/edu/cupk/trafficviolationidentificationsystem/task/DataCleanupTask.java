package edu.cupk.trafficviolationidentificationsystem.task;

import edu.cupk.trafficviolationidentificationsystem.entity.SystemConfig;
import edu.cupk.trafficviolationidentificationsystem.repository.AuditLogMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.SystemConfigMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataCleanupTask {
    private final SystemConfigMapper configMapper;
    private final ViolationMapper violationMapper;
    private final AuditLogMapper logMapper;

    public DataCleanupTask(SystemConfigMapper configMapper,
                           ViolationMapper violationMapper,
                           AuditLogMapper logMapper) {
        this.configMapper = configMapper;
        this.violationMapper = violationMapper;
        this.logMapper = logMapper;
    }

    // 每天凌晨1点执行一次
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanExpiredData() {
        SystemConfig config = configMapper.getConfig();
        int days = config.getDataRetentionDays();

        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        System.out.println("开始清理 " + threshold + " 之前的数据");

        int violationsDeleted = violationMapper.deleteBefore(threshold);
        int logsDeleted = logMapper.deleteBefore(threshold);

        System.out.printf("清理完成：删除违法记录 %d 条，操作日志 %d 条\n", violationsDeleted, logsDeleted);
    }



}
