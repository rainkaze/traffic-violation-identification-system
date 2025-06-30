package edu.cupk.trafficviolationidentificationsystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * Redis 配置类.
 * <p>
 * 本类负责配置核心的 {@link RedisTemplate} Bean，它是 Spring 应用中与 Redis
 * 进行交互的主要工具。通过自定义序列化器，我们确保了 Redis 键（keys）的可读性
 * 和值（values）的灵活性。
 * </p>
 */
@Configuration
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    /**
     * 创建并配置一个定制化的 RedisTemplate Bean.
     * <p>
     * 这个 Bean 做了以下关键配置：
     * <ul>
     * <li><b>Key Serializer</b>: 使用 {@link StringRedisSerializer}。
     * 这能确保存入 Redis 的 key 是人类可读的字符串，非常便于调试和管理。</li>
     * <li><b>Value Serializer</b>: 使用 {@link GenericJackson2JsonRedisSerializer}。
     * 这允许我们将任意 Java 对象（POJO）序列化成 JSON 字符串存储，并在取出时自动
     * 反序列化为正确的 Java 对象，极大地增强了 Redis 作为缓存的灵活性。</li>
     * </ul>
     * </p>
     *
     * @param connectionFactory Spring Boot 自动配置的 Redis 连接工厂，由框架注入。
     * @return 一个完全配置好的 {@code RedisTemplate<String, Object>} 实例。
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("正在配置 RedisTemplate Bean...");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 创建并设置 Key 的序列化器为 StringRedisSerializer
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        log.info("RedisTemplate -> Key/HashKey 序列化器设置为 StringRedisSerializer。");

        // 创建并设置 Value 的序列化器为 GenericJackson2JsonRedisSerializer
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        log.info("RedisTemplate -> Value/HashValue 序列化器设置为 GenericJackson2JsonRedisSerializer。");

        template.afterPropertiesSet();
        log.info("RedisTemplate Bean 配置完成。");
        return template;
    }
}