package edu.cupk.trafficviolationidentificationsystem.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//阿里云配置类 自定义的！
@Component
//读取配置文件中的阿里云配置 封装为java对象
@ConfigurationProperties(prefix = "traffic.alioss")
@Data
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
