package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * 邮件发送消费者服务。
 * 监听RabbitMQ队列，异步处理发送验证码邮件的任务。
 */
@Service
public class EmailConsumerService {

    private static final Logger log = LoggerFactory.getLogger(EmailConsumerService.class);

    private final AuthService authService;

    public EmailConsumerService(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 监听邮件验证码队列。
     * @param email 从队列中接收到的目标邮箱地址。
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL_VERIFICATION)
    public void sendVerificationCode(String email) {
        log.info(" [x] 从队列接收到邮件发送任务，目标邮箱: {}", email);
        try {
            // 调用现有的AuthService来执行实际的发送逻辑
            authService.sendVerificationCode(email);
            log.info("✅ 成功为邮箱 {} 发送了验证码。", email);
        } catch (Exception e) {
            log.error("❌ 异步发送验证码邮件失败，目标邮箱: {}", email, e);
        }
    }
}