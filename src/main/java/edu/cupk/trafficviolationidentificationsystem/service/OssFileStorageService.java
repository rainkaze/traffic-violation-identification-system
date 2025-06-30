package edu.cupk.trafficviolationidentificationsystem.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssFileStorageService {

    @Value("${traffic.alioss.endpoint}")
    private String endpoint;
    @Value("${traffic.alioss.access-key-id}")
    private String accessKeyId;
    @Value("${traffic.alioss.access-key-secret}")
    private String accessKeySecret;
    @Value("${traffic.alioss.bucket-name}")
    private String bucketName;

    /**
     * 上传文件到阿里云OSS，并返回可公开访问的URL。
     * (此方法内部逻辑无需改动)
     *
     * @param file 要上传的文件
     * @return 文件的公开访问URL
     */
    public String storeFile(MultipartFile file) {
        // 1. 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 2. 生成一个唯一的文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectName = "uploads/" + UUID.randomUUID().toString() + fileExtension;

        try (InputStream inputStream = file.getInputStream()) {
            // 3. 执行上传
            ossClient.putObject(bucketName, objectName, inputStream);
        } catch (IOException e) {
            throw new RuntimeException("文件上传到OSS失败", e);
        } finally {
            // 4. 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 5. 拼接并返回文件的公开访问URL
        return "https://" + bucketName + "." + endpoint + "/" + objectName;
    }
}