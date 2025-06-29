package edu.cupk.trafficviolationidentificationsystem.task;

import edu.cupk.trafficviolationidentificationsystem.repository.NotificationSettingMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
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
    public void sendDailySecurityReports() {

        List<Integer> userIds = notificationSettingMapper.getUserIdsByTypeKey("daily_security_report");
        for (Integer userId : userIds) {
            String email = userMapper.findEmailByUserId(userId); // 假设根据 userId 获取邮箱地址
            String subject = "每日安全报告";
            String content = "这是您今日的安全报告，请注意查看系统状态。";
            emailService.sendSimpleMessage(email, subject, content);
        }

        System.out.println("每日安全报告发送完成，用户数：" + userIds.size());
    }

    /**
     * 每周数据摘要：每周一凌晨 2 点执行
     */
    @Scheduled(cron = "0 0 2 ? * MON")
    public void sendWeeklySummaries() {
        List<Integer> userIds = notificationSettingMapper.getUserIdsByTypeKey("weekly_summary");

        for (Integer userId : userIds) {
            String email = userMapper.findEmailByUserId(userId);
            String subject = "每周数据摘要";
            String content = "这是您本周的系统运行数据摘要，请及时查看。";
            emailService.sendSimpleMessage(email, subject, content);
        }

        System.out.println("每周数据摘要发送完成，用户数：" + userIds.size());
    }

}
