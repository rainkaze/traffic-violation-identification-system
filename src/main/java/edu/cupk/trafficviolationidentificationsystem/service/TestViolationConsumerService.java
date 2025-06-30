package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.config.RabbitMQConfig;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationMessageDto;
import edu.cupk.trafficviolationidentificationsystem.entity.Notification; // <-- 【修正】使用正确的 model 包
import edu.cupk.trafficviolationidentificationsystem.model.Violation;
import edu.cupk.trafficviolationidentificationsystem.entity.WarningRule;
import edu.cupk.trafficviolationidentificationsystem.repository.NotificationMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.NotificationSettingMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.WarningRuleMapper;
import edu.cupk.trafficviolationidentificationsystem.util.ByteArrayMultipartFile;
import edu.cupk.trafficviolationidentificationsystem.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime; // <-- 【新增】导入 LocalDateTime
import java.util.Comparator;
import java.util.List;

/**
 * 【最终版】消费者服务
 * 1. 处理接收的违章数据。
 * 2. 评估预警。
 * 3. 将通知存入数据库（收件箱）。
 * 4. 尝试进行实时WebSocket推送。
 */
@Service
public class TestViolationConsumerService {

    private static final Logger log = LoggerFactory.getLogger(TestViolationConsumerService.class);

    // --- 【新增】注入 NotificationMapper 用于写入数据库 ---
    private final NotificationMapper notificationMapper;
    private final ViolationService violationService;
    private final ViolationMapper violationMapper;
    private final WarningRuleMapper warningRuleMapper;
    private final NotificationSettingMapper notificationSettingMapper;
    private final WebSocketServer webSocketServer;

    /**
     * 【更新】使用构造函数注入所有依赖。
     */
    public TestViolationConsumerService(ViolationService violationService, ViolationMapper violationMapper, WarningRuleMapper warningRuleMapper, NotificationSettingMapper notificationSettingMapper, WebSocketServer webSocketServer, NotificationMapper notificationMapper) {
        this.violationService = violationService;
        this.violationMapper = violationMapper;
        this.warningRuleMapper = warningRuleMapper;
        this.notificationSettingMapper = notificationSettingMapper;
        this.webSocketServer = webSocketServer;
        this.notificationMapper = notificationMapper; // 注入
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_VIOLATION_TEST)
    @Transactional
    public void processTestViolation(ViolationMessageDto messageDto) {
        try {
            log.info(" [x] 从队列接收到测试违章数据，规则ID: {}", messageDto.getViolationData().getRuleId());
            MultipartFile evidenceImage = new ByteArrayMultipartFile(
                    messageDto.getImageBytes(), "evidenceImage", messageDto.getOriginalImageName(), messageDto.getImageContentType()
            );

            Violation createdViolation = violationService.createTestViolation(messageDto.getViolationData(), evidenceImage);
            log.info("✅ 成功保存了测试违章数据，数据库ID: {}", createdViolation.getViolationId());

            if (messageDto.getViolationData().getConfidenceScore() != null) {
                evaluateAndNotifyWarning(
                        createdViolation.getViolationId(),
                        messageDto.getViolationData().getConfidenceScore()
                );
            }
        } catch (Exception e) {
            log.error("❌ 处理队列中的测试违章数据失败。错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 【增强版】评估违章记录，并进行持久化+实时通知
     */
    private void evaluateAndNotifyWarning(Long violationId, Double confidenceScore) {
        ViolationDetailDto detail = violationMapper.findViolationDetailById(violationId);
        if (detail == null) {
            log.warn("无法为ID: {} 找到违章详情，跳过预警评估。", violationId);
            return;
        }

        List<WarningRule> rules = warningRuleMapper.findAll();
        rules.sort(Comparator.comparingInt(this::getLevelWeight));

        for (WarningRule rule : rules) {
            if (detail.getType().contains(rule.getViolationType()) && confidenceScore >= rule.getMinConfidence()) {
                log.info("触发预警！违章ID: {}, 规则: {}, 置信度: {}, 预警等级: {}",
                        violationId, detail.getType(), confidenceScore, rule.getLevel());

                if ("一级预警".equals(rule.getLevel())) {
                    String message = String.format("""
                        【一级预警通知】车牌号：%s, 违法类型：%s, 时间：%s, 地点：%s
                        """,
                            detail.getPlate(), detail.getType(), detail.getTime(),
                            detail.getLocation() != null ? detail.getLocation() : "未知");

                    List<Integer> userIdsToNotify = notificationSettingMapper.getUserIdsByTypeKey("alert_level_one");

                    // --- 【核心逻辑】遍历需要通知的用户 ---
                    for(Integer userId : userIdsToNotify) {
                        // 1. 【持久化】将通知写入数据库，确保用户无论如何都能收到
                        Notification notification = Notification.builder()
                                .userId(Long.valueOf(userId))
                                .message(message)
                                .isRead(false) // 标记为未读
                                .timestamp(LocalDateTime.now())
                                .build();
                        notificationMapper.insertNotification(notification);
                        log.info("  -> 通知已为用户 {} 存入数据库收件箱。", userId);
                    }

                    // 2. 【实时推送】尝试向所有目标用户进行实时推送
                    log.info("  -> 准备向用户列表 {} 尝试实时推送...", userIdsToNotify);
                    webSocketServer.sendToClientsByInt(userIdsToNotify, message);
                }

                break;
            }
        }
    }

    private int getLevelWeight(WarningRule rule) {
        return switch (rule.getLevel()) {
            case "一级预警" -> 1;
            case "二级预警" -> 2;
            case "三级预警" -> 3;
            default -> 99;
        };
    }
}