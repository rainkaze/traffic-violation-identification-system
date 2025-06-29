package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.config.RabbitMQConfig;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationMessageDto;
import edu.cupk.trafficviolationidentificationsystem.util.ByteArrayMultipartFile; // 1. 导入我们自己的实现类
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 消费者服务，用于处理从消息队列接收的测试违章数据。
 */
@Service
public class TestViolationConsumerService {

    // 3. 使用标准的SLF4J日志记录器
    private static final Logger log = LoggerFactory.getLogger(TestViolationConsumerService.class);

    private final ViolationService violationService;

    /**
     * 构造函数注入，这是Spring推荐的最佳实践。
     * @param violationService 违法记录服务。
     */
    public TestViolationConsumerService(ViolationService violationService) {
        this.violationService = violationService;
    }

    /**
     * 监听测试违章数据队列，并处理接收到的消息。
     * @param messageDto 包含违章数据和图片字节的消息对象。
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_VIOLATION_TEST)
    public void processTestViolation(ViolationMessageDto messageDto) {
        log.info(" [x] 从队列接收到测试违章数据，规则ID: {}", messageDto.getViolationData().getRuleId());
        try {
            // 2. 使用我们自己的 ByteArrayMultipartFile 来重建文件对象
            MultipartFile evidenceImage = new ByteArrayMultipartFile(
                    messageDto.getImageBytes(),
                    "evidenceImage",
                    messageDto.getOriginalImageName(),
                    messageDto.getImageContentType()
            );

            // 调用原始的service方法，现在它可以无缝接收我们创建的文件对象
            violationService.createTestViolation(messageDto.getViolationData(), evidenceImage);

            log.info("✅ 成功处理并保存了测试违章数据，规则ID: {}", messageDto.getViolationData().getRuleId());

        } catch (Exception e) {
            // 4. 使用日志记录器记录错误，而不是打印堆栈跟踪
            log.error("❌ 处理队列中的测试违章数据失败。错误: {}", e.getMessage(), e);
        }
    }
}