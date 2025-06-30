package edu.cupk.trafficviolationidentificationsystem.config;

import edu.cupk.trafficviolationidentificationsystem.service.RedisMessageSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Redis 发布/订阅 (Pub/Sub) 功能配置.
 * <p>
 * 本类负责设置 Redis 的消息监听器容器，使其能够订阅指定的频道 (Channel/Topic)
 * 并将接收到的消息分发给相应的处理器。
 * </p>
 */
@Configuration
public class RedisPubSubConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisPubSubConfig.class);

    /**
     * 定义用于接收通知消息的 Redis 频道名称。
     */
    public static final String NOTIFICATION_TOPIC = "notifications";

    /**
     * 创建一个消息监听器适配器 (MessageListenerAdapter).
     * <p>
     * 这个适配器将我们自定义的 {@link RedisMessageSubscriber} 包装起来，
     * 使其能够被 {@link RedisMessageListenerContainer} 集成。
     * 适配器会自动将收到的消息委托给 subscriber 的默认处理方法 (handleMessage)。
     * </p>
     *
     * @param subscriber 我们自定义的消息订阅者服务，由 Spring 注入。
     * @return 一个配置好的 MessageListenerAdapter 实例。
     */
    @Bean
    MessageListenerAdapter messageListener(RedisMessageSubscriber subscriber) {
        log.info("创建 MessageListenerAdapter Bean，包装服务: {}", subscriber.getClass().getName());
        return new MessageListenerAdapter(subscriber);
    }

    /**
     * 创建并配置 Redis 消息监听器容器 (RedisMessageListenerContainer).
     * <p>
     * 这是 Redis Pub/Sub 功能的核心。它负责：
     * <ul>
     * <li>管理与 Redis 服务器的连接。</li>
     * <li>监听一个或多个指定的频道。</li>
     * <li>在收到消息时，调用已注册的监听器 (通过 MessageListenerAdapter)。</li>
     * </ul>
     * </p>
     *
     * @param connectionFactory    Redis 连接工厂，由 Spring 自动注入。
     * @param messageListenerAdapter 消息监听器适配器 Bean，由 Spring 注入。
     * @return 一个运行中的 RedisMessageListenerContainer 实例。
     */
    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
        log.info("正在配置 RedisMessageListenerContainer Bean...");
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 将监听器适配器添加到容器，并指定监听的频道
        container.addMessageListener(messageListenerAdapter, new ChannelTopic(NOTIFICATION_TOPIC));
        log.info("消息监听器已成功订阅 Redis 频道: '{}'", NOTIFICATION_TOPIC);

        return container;
    }
}