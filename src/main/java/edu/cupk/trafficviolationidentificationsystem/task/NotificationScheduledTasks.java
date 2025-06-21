package edu.cupk.trafficviolationidentificationsystem.task;

import edu.cupk.trafficviolationidentificationsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationScheduledTasks {
//    @Autowired
//    private NotificationSettingService notificationSettingService;

    @Autowired
    private EmailService emailService;

    // 每天凌晨1点执行
    @Scheduled(cron = "0 0 1 * * ?")
    public void sendDailySecurityReports() {
//        List<Integer> userIds = notificationSettingService.getUsersWithEnabled("daily_security_report");
//
//        for (Integer userId : userIds) {
//            emailService.sendDailySecurityReport(userId);
//        }
    }

    // 每周一凌晨2点执行
    @Scheduled(cron = "0/5 * * * * ?")
    public void sendWeeklySummaries() {
//        System.out.println("定时任务！！！！！！！");
//        List<Integer> userIds = notificationSettingService.getUsersWithEnabled("weekly_summary");
//
//        for (Integer userId : userIds) {
//            emailService.sendWeeklySummary(userId);
//        }
    }


}
