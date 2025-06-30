package edu.cupk.trafficviolationidentificationsystem.task;

import edu.cupk.trafficviolationidentificationsystem.dto.ViolationReportDto;
import edu.cupk.trafficviolationidentificationsystem.repository.NotificationSettingMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import edu.cupk.trafficviolationidentificationsystem.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 计划性通知任务。
 * <p>
 * 包含所有基于时间的、自动发送通知的计划任务，例如每日和每周的安全报告。
 * </p>
 */
@Component
public class NotificationScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(NotificationScheduledTasks.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ViolationMapper violationMapper;

    private final NotificationSettingMapper notificationSettingMapper;
    private final EmailService emailService;

    public NotificationScheduledTasks(NotificationSettingMapper notificationSettingMapper,
                                      EmailService emailService) {
        this.notificationSettingMapper = notificationSettingMapper;
        this.emailService = emailService;
    }

    /**
     * 发送每日安全报告。
     * <p>
     * Cron 表达式 "0 0 1 * * ?" 表示此任务将在每天凌晨 1:00:00 执行。
     * 它会收集前一天的所有违规记录，并以邮件形式发送给订阅了 "daily_security_report" 的所有用户。
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void sendDailySecurityReports() {
        log.info("开始执行每日安全报告发送任务...");
        try {
            List<Integer> userIds = notificationSettingMapper.getUserIdsByTypeKey("daily_security_report");
            if (userIds.isEmpty()) {
                log.info("没有用户订阅每日安全报告，任务结束。");
                return;
            }

            List<ViolationReportDto> dailyViolations = violationMapper.findViolationsOfYesterday();
            StringBuilder content = new StringBuilder("【每日安全报告】昨日（" + LocalDate.now().minusDays(1) + "）违规记录如下：\n\n");

            if (dailyViolations.isEmpty()) {
                content.append("昨日无新增违规记录，系统运行状况良好！\n");
            } else {
                content.append(String.format("%-12s | %-10s | %-19s | %-6s | %-6s\n",
                        "车牌号", "违法类型", "违法时间", "地点", "状态"));
                content.append("--------------------------------------------------------------\n");
                for (ViolationReportDto v : dailyViolations) {
                    content.append(String.format("%-12s | %-10s | %-19s | %-6s | %-6s\n",
                            v.getPlateNumber(), v.getViolationType(), v.getViolationTime().toString(),
                            v.getLocation() != null ? v.getLocation() : "未知", v.getStatus()));
                }
            }

            String subject = "【每日安全报告】" + LocalDate.now().minusDays(1);
            int emailsSent = 0;
            for (Integer userId : userIds) {
                String email = userMapper.findEmailByUserId(userId);
                if (email != null && !email.isEmpty()) {
                    emailService.sendSimpleMessage(email, subject, content.toString());
                    emailsSent++;
                } else {
                    log.warn("无法为用户 ID '{}' 发送每日报告，因为未找到其邮箱地址。", userId);
                }
            }
            log.info("每日安全报告发送任务完成，共向 {} 位用户发送了邮件。", emailsSent);
        } catch (Exception e) {
            log.error("发送每日安全报告时发生错误。", e);
        }
    }

    /**
     * 发送每周数据摘要。
     * <p>
     * Cron 表达式 "0 0 2 ? * MON" 表示此任务将在每周一的凌晨 2:00:00 执行。
     * 它会汇总上一周的违规记录，并发送邮件给订阅了 "weekly_summary" 的用户。
     * </p>
     */
    @Scheduled(cron = "0 0 2 ? * MON")
    public void sendWeeklySummaries() {
        log.info("开始执行每周数据摘要发送任务...");
        try {
            List<Integer> userIds = notificationSettingMapper.getUserIdsByTypeKey("weekly_summary");
            if (userIds.isEmpty()) {
                log.info("没有用户订阅每周数据摘要，任务结束。");
                return;
            }

            List<ViolationReportDto> weeklyViolations = violationMapper.findViolationsOfLastWeek();
            StringBuilder content = new StringBuilder("【每周数据摘要】上周违规记录汇总：\n\n");

            if (weeklyViolations.isEmpty()) {
                content.append("上周无新增违规记录，系统运行状况良好！\n");
            } else {
                content.append(String.format("%-12s | %-10s | %-19s | %-6s | %-6s\n",
                        "车牌号", "违法类型", "违法时间", "地点", "状态"));
                content.append("--------------------------------------------------------------\n");
                for (ViolationReportDto v : weeklyViolations) {
                    content.append(String.format("%-12s | %-10s | %-19s | %-6s | %-6s\n",
                            v.getPlateNumber(), v.getViolationType(), v.getViolationTime().toString(),
                            v.getLocation() != null ? v.getLocation() : "未知", v.getStatus()));
                }
            }

            String subject = "【每周数据摘要】" + LocalDate.now().minusWeeks(1);
            int emailsSent = 0;
            for (Integer userId : userIds) {
                String email = userMapper.findEmailByUserId(userId);
                if (email != null && !email.isEmpty()) {
                    emailService.sendSimpleMessage(email, subject, content.toString());
                    emailsSent++;
                } else {
                    log.warn("无法为用户 ID '{}' 发送每周摘要，因为未找到其邮箱地址。", userId);
                }
            }
            log.info("每周数据摘要发送任务完成，共向 {} 位用户发送了邮件。", emailsSent);
        } catch (Exception e) {
            log.error("发送每周数据摘要时发生错误。", e);
        }
    }
}