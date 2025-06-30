package edu.cupk.trafficviolationidentificationsystem.task;

import edu.cupk.trafficviolationidentificationsystem.dto.ViolationReportDto;
import edu.cupk.trafficviolationidentificationsystem.repository.NotificationSettingMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import edu.cupk.trafficviolationidentificationsystem.service.EmailService;
import edu.cupk.trafficviolationidentificationsystem.util.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationScheduledTasks {

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
     * 每日安全报告：每天凌晨 1 点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(cron = "0/1 * * * * ?")
    public void sendDailySecurityReports() {
        List<ViolationReportDto> dailyViolations = violationMapper.findViolationsOfYesterday();
        StringBuilder content = new StringBuilder();
        content.append("【每日安全报告】昨日违法记录如下：\n\n");

        if (dailyViolations.isEmpty()) {
            content.append("昨日无违法记录，安全运行良好！\n");
        } else {
            content.append(String.format("%-12s | %-10s | %-19s | %-6s | %-6s\n",
                    "车牌号", "违法类型", "违法时间", "地点", "状态"));
            content.append("--------------------------------------------------------------\n");
            for (ViolationReportDto v : dailyViolations) {
                content.append(String.format("%-12s | %-10s | %-19s | %-6s | %-6s\n",
                        v.getPlateNumber(),
                        v.getViolationType(),
                        v.getViolationTime().toString(),
                        v.getLocation() != null ? v.getLocation() : "未知",
                        v.getStatus()));
            }
        }

        String subject = "【每日安全报告】" + java.time.LocalDate.now().minusDays(1).toString();
        List<Integer> userIds = notificationSettingMapper.getUserIdsByTypeKey("daily_security_report");
        for (Integer userId : userIds) {
            String email = userMapper.findEmailByUserId(userId);
            emailService.sendSimpleMessage(email, subject, content.toString());
        }

        System.out.println("每日安全报告发送完成，用户数：" + userIds.size());




    }

    /**
     * 每周数据摘要：每周一凌晨 2 点执行
     */
    @Scheduled(cron = "0 0 2 ? * MON")
//    @Scheduled(cron = "0/1 * * * * ?")
    public void sendWeeklySummaries() {

        List<ViolationReportDto> weeklyViolations = violationMapper.findViolationsOfLastWeek();
        StringBuilder content = new StringBuilder();
        content.append("【每周安全报告】上周违法记录如下：\n\n");

        if (weeklyViolations.isEmpty()) {
            content.append("上周无违法记录，安全运行良好！\n");
        } else {
            content.append(String.format("%-12s | %-10s | %-19s | %-6s | %-6s\n",
                    "车牌号", "违法类型", "违法时间", "地点", "状态"));
            content.append("--------------------------------------------------------------\n");
            for (ViolationReportDto v : weeklyViolations) {
                content.append(String.format("%-12s | %-10s | %-19s | %-6s | %-6s\n",
                        v.getPlateNumber(),
                        v.getViolationType(),
                        v.getViolationTime().toString(),
                        v.getLocation() != null ? v.getLocation() : "未知",
                        v.getStatus()));
            }
        }

        String subject = "【每周安全报告】" + java.time.LocalDate.now().minusWeeks(1).toString();
        List<Integer> userIds = notificationSettingMapper.getUserIdsByTypeKey("weekly_summary");
        for (Integer userId : userIds) {
            String email = userMapper.findEmailByUserId(userId);
            emailService.sendSimpleMessage(email, subject, content.toString());
        }

        System.out.println("每周安全报告发送完成，用户数：" + userIds.size());

    }

}
