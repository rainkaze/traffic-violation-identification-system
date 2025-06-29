package edu.cupk.trafficviolationidentificationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.cupk.trafficviolationidentificationsystem.config.RabbitMQConfig;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationMessageDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationTestDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final ObjectMapper objectMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate; // 注入RabbitTemplate

    public TestController() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 异步接收违章数据和证据图片。
     * 此接口会立刻将数据放入消息队列并返回，由后台服务进行处理。
     */
    @PostMapping("/submit-violation")
    public ResponseEntity<?> submitViolation(
            @RequestPart("violationData") String violationDataJson,
            @RequestPart("evidenceImage") MultipartFile evidenceImage) {
        try {
            // 1. 将JSON字符串反序列化为DTO
            ViolationTestDto violationDto = objectMapper.readValue(violationDataJson, ViolationTestDto.class);

            // 2. 将图片内容和违章数据打包成一个消息对象
            ViolationMessageDto messageDto = new ViolationMessageDto(
                    violationDto,
                    evidenceImage.getBytes(),
                    evidenceImage.getOriginalFilename(),
                    evidenceImage.getContentType()
            );

            // 3. 将消息对象发送到RabbitMQ
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_VIOLATION_TEST,
                    messageDto
            );

            // 4. 立刻返回一个“已接受”的响应
            return ResponseEntity.accepted().body("Violation data has been queued for processing.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error queuing request: " + e.getMessage());
        }
    }
}