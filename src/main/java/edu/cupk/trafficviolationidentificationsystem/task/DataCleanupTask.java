package edu.cupk.trafficviolationidentificationsystem.task;

import edu.cupk.trafficviolationidentificationsystem.entity.SystemConfig;
import edu.cupk.trafficviolationidentificationsystem.repository.AuditLogMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.SystemConfigMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时数据清理任务。
 * <p>
 * 使用 Spring 的 {@link Scheduled} 注解，定期执行数据清理操作。
 * 它会根据系统配置中的数据保留天数，删除过期的违规记录和审计日志。
 * </p>
 */
@Component
public class DataCleanupTask {
    private static final Logger log = LoggerFactory.getLogger(DataCleanupTask.class);

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

    /**
     * 执行数据清理。
     * <p>
     * Cron 表达式 "0 0 1 * * ?" 表示此任务将在每天凌晨 1:00:00 执行。
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanExpiredData() {
        log.info("开始执行每日数据清理任务...");
        try {
            SystemConfig config = configMapper.getConfig();
            if (config == null || config.getDataRetentionDays() <= 0) {
                log.warn("数据清理任务中止：未找到有效的系统配置或数据保留天数未设置。");
                return;
            }

            int days = config.getDataRetentionDays();
            LocalDateTime threshold = LocalDateTime.now().minusDays(days);
            log.info("根据配置，将清理 {} 天前的数据 (即 {} 之前的所有记录)。", days, threshold);

            int violationsDeleted = violationMapper.deleteBefore(threshold);
            int logsDeleted = logMapper.deleteBefore(threshold);

            log.info("数据清理任务完成：共删除 {} 条违规记录和 {} 条操作日志。", violationsDeleted, logsDeleted);
        } catch (Exception e) {
            log.error("执行数据清理任务时发生严重错误。", e);
        }
    }
}