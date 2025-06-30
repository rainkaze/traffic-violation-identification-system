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

/**
 * RabbitMQ 消息队列配置.
 * <p>
 * 本类负责定义与 RabbitMQ 相关的核心组件，包括交换机 (Exchange)、队列 (Queue)、
 * 绑定 (Binding) 以及消息的序列化/反序列化方式。
 * </p>
 */
@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    // --- 公共组件定义 ---

    /**
     * 定义项目中使用的主要交换机名称。
     * 这是一个 Topic 类型的交换机，能够根据路由键 (Routing Key) 将消息灵活地分发到不同的队列。
     */
    public static final String EXCHANGE_NAME = "traffic_exchange";

    // --- 通知功能相关队列和路由键 ---

    /**
     * 用于实时通知的队列名称。
     */
    public static final String QUEUE_NOTIFICATION = "q.notification";

    /**
     * 用于绑定“通知队列”的路由键模式。
     * "#" 表示匹配一个或多个词，所以任何以 "routing.notification." 开头的路由键的消息都会被送到该队列。
     */
    public static final String ROUTING_KEY_NOTIFICATION = "routing.notification.#";

    // --- 违章测试相关队列和路由键 ---

    /**
     * 用于处理违章识别测试请求的队列名称。
     */
    public static final String QUEUE_VIOLATION_TEST = "q.violation.test";

    /**
     * 用于将“违章测试提交”消息路由到对应队列的精确路由键。
     */
    public static final String ROUTING_KEY_VIOLATION_TEST = "routing.violation.test.submit";

    /**
     * 定义消息转换器 Bean。
     * <p>
     * 使用 {@link Jackson2JsonMessageConverter} 会将 Spring Boot 应用中的 Java 对象
     * 自动序列化为 JSON 格式的字符串进行传输，并在消费端自动反序列化回 Java 对象。
     * 这极大地简化了复杂数据结构的传递。
     * </p>
     *
     * @return 一个 Jackson JSON 消息转换器实例。
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        log.info("创建 RabbitMQ MessageConverter Bean (Jackson2JsonMessageConverter)。");
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定义一个主题交换机 (Topic Exchange)。
     *
     * @return 一个名为 {@value #EXCHANGE_NAME} 的 TopicExchange 实例。
     */
    @Bean
    public TopicExchange exchange() {
        log.info("创建 RabbitMQ TopicExchange Bean: '{}'", EXCHANGE_NAME);
        return new TopicExchange(EXCHANGE_NAME);
    }

    /**
     * 定义“通知队列”。
     *
     * @return 一个名为 {@value #QUEUE_NOTIFICATION} 的 Queue 实例。
     */
    @Bean
    public Queue notificationQueue() {
        log.info("创建 RabbitMQ Queue Bean: '{}'", QUEUE_NOTIFICATION);
        return new Queue(QUEUE_NOTIFICATION);
    }

    /**
     * 将“通知队列”绑定到交换机。
     * <p>
     * 使用路由键 {@value #ROUTING_KEY_NOTIFICATION} 将 {@code notificationQueue}
     * 绑定到 {@code exchange}。
     * </p>
     *
     * @param notificationQueue 通知队列的 Bean 注入。
     * @param exchange          主题交换机的 Bean 注入。
     * @return 一个 Binding 实例。
     */
    @Bean
    public Binding notificationBinding(Queue notificationQueue, TopicExchange exchange) {
        log.info("创建 RabbitMQ Binding: 将 Queue '{}' 绑定到 Exchange '{}' (使用路由键 '{}')", QUEUE_NOTIFICATION, EXCHANGE_NAME, ROUTING_KEY_NOTIFICATION);
        return BindingBuilder.bind(notificationQueue).to(exchange).with(ROUTING_KEY_NOTIFICATION);
    }

    /**
     * 定义“违章测试队列”。
     *
     * @return 一个名为 {@value #QUEUE_VIOLATION_TEST} 的 Queue 实例。
     */
    @Bean
    public Queue violationTestQueue() {
        log.info("创建 RabbitMQ Queue Bean: '{}'", QUEUE_VIOLATION_TEST);
        return new Queue(QUEUE_VIOLATION_TEST);
    }

    /**
     * 将“违章测试队列”绑定到交换机。
     * <p>
     * 使用路由键 {@value #ROUTING_KEY_VIOLATION_TEST} 将 {@code violationTestQueue}
     * 绑定到 {@code exchange}。
     * </p>
     *
     * @param violationTestQueue 违章测试队列的 Bean 注入。
     * @param exchange           主题交换机的 Bean 注入。
     * @return 一个 Binding 实例。
     */
    @Bean
    public Binding violationTestBinding(Queue violationTestQueue, TopicExchange exchange) {
        log.info("创建 RabbitMQ Binding: 将 Queue '{}' 绑定到 Exchange '{}' (使用路由键 '{}')", QUEUE_VIOLATION_TEST, EXCHANGE_NAME, ROUTING_KEY_VIOLATION_TEST);
        return BindingBuilder.bind(violationTestQueue).to(exchange).with(ROUTING_KEY_VIOLATION_TEST);
    }
}