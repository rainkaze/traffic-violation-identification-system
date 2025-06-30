package edu.cupk.trafficviolationidentificationsystem.config;

import edu.cupk.trafficviolationidentificationsystem.properties.AliOssProperties;
import edu.cupk.trafficviolationidentificationsystem.util.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云对象存储 (OSS) 配置类.
 * <p>
 * 负责创建并配置 {@link AliOssUtil} 的 Spring Bean,
 * 该工具类用于简化文件上传、下载等与阿里云OSS的交互操作。
 * </p>
 */
@Configuration
@Slf4j // Lombok提供的注解，自动创建一个名为`log`的SLF4J Logger实例
public class OssConfiguration {

    /**
     * 创建 AliOssUtil 的 Bean.
     * <p>
     * 当Spring容器中不存在 AliOssUtil 类型的Bean时，此方法会被调用以创建一个新的实例。
     * 它会从 {@link AliOssProperties} 中读取OSS的配置信息（如endpoint, accessKey等）
     * 并用这些信息来初始化工具类。
     * </p>
     *
     * @param aliOssProperties 自动注入的OSS配置属性类，其值来源于application配置文件。
     * @return 配置完成的 AliOssUtil 实例。
     */
    @Bean
    @ConditionalOnMissingBean // 保证容器里只有一个此类型的Bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("正在创建 AliOssUtil Bean...");
        AliOssUtil aliOssUtil = new AliOssUtil(
                aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName()
        );
        log.info("AliOssUtil Bean 创建成功, 使用的 bucket 名称为: '{}'", aliOssProperties.getBucketName());
        return aliOssUtil;
    }
}