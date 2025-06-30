package edu.cupk.trafficviolationidentificationsystem.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云对象存储 (OSS) 配置属性类.
 * <p>
 * 使用 {@link ConfigurationProperties} 注解，将配置文件中前缀为
 * {@code traffic.alioss} 的属性值自动绑定到该类的字段上。
 * </p>
 *
 * 示例 (application.properties):
 * <pre>
 * traffic.alioss.endpoint=oss-cn-beijing.aliyuncs.com
 * traffic.alioss.access-key-id=your_access_key_id
 * traffic.alioss.access-key-secret=your_access_key_secret
 * traffic.alioss.bucket-name=your_bucket_name
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "traffic.alioss")
@Data
public class AliOssProperties {

    /**
     * OSS 服务接入点 (Endpoint)。例如: oss-cn-beijing.aliyuncs.com
     */
    private String endpoint;

    /**
     * 访问 OSS 的 AccessKey ID。
     */
    private String accessKeyId;

    /**
     * 访问 OSS 的 AccessKey Secret。
     */
    private String accessKeySecret;

    /**
     * 要操作的存储空间 (Bucket) 名称。
     */
    private String bucketName;
}