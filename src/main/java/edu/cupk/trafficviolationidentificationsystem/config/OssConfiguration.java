package edu.cupk.trafficviolationidentificationsystem.config;



import edu.cupk.trafficviolationidentificationsystem.properties.AliOssProperties;
import edu.cupk.trafficviolationidentificationsystem.util.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OssConfiguration {
//    参数是配置属性类 依赖注入
    @Bean
    @ConditionalOnMissingBean
     public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());

    }
}
