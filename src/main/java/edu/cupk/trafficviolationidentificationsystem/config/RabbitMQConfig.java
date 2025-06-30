package edu.cupk.trafficviolationidentificationsystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    public static final String EXCHANGE_NAME = "traffic_exchange";

    // --- 通知功能相关 ---
    public static final String QUEUE_NOTIFICATION = "q.notification";
    public static final String ROUTING_KEY_NOTIFICATION = "routing.notification.#";

    // --- 违章测试相关 ---
    public static final String QUEUE_VIOLATION_TEST = "q.violation.test";
    public static final String ROUTING_KEY_VIOLATION_TEST = "routing.violation.test.submit";

    // --- 【新增】邮件发送相关 ---
    public static final String QUEUE_EMAIL_VERIFICATION = "q.email.verification";
    public static final String ROUTING_KEY_EMAIL_VERIFICATION = "routing.email.verification.send";

    @Bean
    public MessageConverter jsonMessageConverter() {
        log.info("创建 RabbitMQ MessageConverter Bean (Jackson2JsonMessageConverter)。");
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange exchange() {
        log.info("创建 RabbitMQ TopicExchange Bean: '{}'", EXCHANGE_NAME);
        return new TopicExchange(EXCHANGE_NAME);
    }

    // --- 【新增】邮件发送队列的Bean定义 ---
    @Bean
    public Queue emailVerificationQueue() {
        log.info("创建 RabbitMQ Queue Bean: '{}'", QUEUE_EMAIL_VERIFICATION);
        return new Queue(QUEUE_EMAIL_VERIFICATION);
    }

    @Bean
    public Queue notificationQueue() {
        log.info("创建 RabbitMQ Queue Bean: '{}'", QUEUE_NOTIFICATION);
        return new Queue(QUEUE_NOTIFICATION);
    }

    @Bean
    public Queue violationTestQueue() {
        log.info("创建 RabbitMQ Queue Bean: '{}'", QUEUE_VIOLATION_TEST);
        return new Queue(QUEUE_VIOLATION_TEST);
    }

    // --- 【新增】邮件发送队列的绑定定义 ---
    @Bean
    public Binding emailVerificationBinding(Queue emailVerificationQueue, TopicExchange exchange) {
        log.info("创建 RabbitMQ Binding: 将 Queue '{}' 绑定到 Exchange '{}' (使用路由键 '{}')", QUEUE_EMAIL_VERIFICATION, EXCHANGE_NAME, ROUTING_KEY_EMAIL_VERIFICATION);
        return BindingBuilder.bind(emailVerificationQueue).to(exchange).with(ROUTING_KEY_EMAIL_VERIFICATION);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange exchange) {
        log.info("创建 RabbitMQ Binding: 将 Queue '{}' 绑定到 Exchange '{}' (使用路由键 '{}')", QUEUE_NOTIFICATION, EXCHANGE_NAME, ROUTING_KEY_NOTIFICATION);
        return BindingBuilder.bind(notificationQueue).to(exchange).with(ROUTING_KEY_NOTIFICATION);
    }

    @Bean
    public Binding violationTestBinding(Queue violationTestQueue, TopicExchange exchange) {
        log.info("创建 RabbitMQ Binding: 将 Queue '{}' 绑定到 Exchange '{}' (使用路由键 '{}')", QUEUE_VIOLATION_TEST, EXCHANGE_NAME, ROUTING_KEY_VIOLATION_TEST);
        return BindingBuilder.bind(violationTestQueue).to(exchange).with(ROUTING_KEY_VIOLATION_TEST);
    }
}