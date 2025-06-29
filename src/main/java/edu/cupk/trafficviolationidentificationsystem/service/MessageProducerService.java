package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageProducerService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送通知消息
     * @param notificationPayload 包含通知内容的对象 (可以是DTO或Map)
     */
    public void sendNotification(Object notificationPayload) {
        // 定义一个具体的路由键
        String routingKey = "routing.notification.send";
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, notificationPayload);
        System.out.println(" [x] Sent notification message: '" + notificationPayload + "'");
    }
}