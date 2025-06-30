package edu.cupk.trafficviolationidentificationsystem.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 阿里云对象存储 (OSS) 操作工具类.
 * <p>
 * 封装了对阿里云 OSS 的常用操作，包括文件上传、列出文件和删除文件。
 * 这个类被设计为非 Spring Bean，其实例由 {@link edu.cupk.trafficviolationidentificationsystem.config.OssConfiguration} 创建和管理。
 * </p>
 */
@Data
@AllArgsConstructor
@Slf4j // Lombok's annotation for automatically creating a SLF4J logger instance named 'log'
public class AliOssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /**
     * 将字节数组上传到指定的 OSS 对象。
     *
     * @param bytes      文件的字节数组内容。
     * @param objectName 在 OSS 中存储的对象全名，通常包含路径和文件名 (e.g., "videos/my-video.mp4").
     * @return 上传成功后文件的公开访问 URL。
     */
    public String upload(byte[] bytes, String objectName) {
        // 创建 OSSClient 实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        log.info("准备上传文件到 OSS. Bucket: '{}', ObjectName: '{}'", bucketName, objectName);

        try {
            // 执行上传操作
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            // 这是服务器端异常，表示请求已到达OSS但被拒绝
            log.error("OSS 服务器拒绝了上传请求。ErrorCode: {}, ErrorMessage: {}, RequestId: {}, HostId: {}",
                    oe.getErrorCode(), oe.getErrorMessage(), oe.getRequestId(), oe.getHostId(), oe);
            // Re-throw as a runtime exception to notify the caller of the failure
            throw new RuntimeException("OSS server error occurred during upload.", oe);
        } catch (ClientException ce) {
            // 这是客户端异常，表示客户端在与OSS通信时遇到问题 (e.g., network error)
            log.error("OSS 客户端在尝试上传时遇到内部问题。ErrorMessage: {}", ce.getMessage(), ce);
            throw new RuntimeException("OSS client error occurred during upload.", ce);
        } finally {
            // 确保 OSSClient 被关闭以释放资源
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 构建并返回文件的公共访问 URL
        String fileUrl = "https://" + bucketName + "." + endpoint + "/" + objectName;
        log.info("文件成功上传，访问URL: {}", fileUrl);

        return fileUrl;
    }

    /**
     * 列出指定前缀下的所有备份文件。
     *
     * @param prefix 对象名称的前缀，用于筛选文件 (e.g., "backups/2025/").
     * @return 包含所有匹配文件公共访问 URL 的列表。
     */
    public List<String> listBackupFiles(String prefix) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        log.info("正在列出 OSS Bucket '{}' 中前缀为 '{}' 的文件...", bucketName, prefix);
        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName).withPrefix(prefix);
            ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);

            List<String> fileUrls = objectListing.getObjectSummaries().stream()
                    .map(summary -> "https://" + bucketName + "." + endpoint + "/" + summary.getKey())
                    .collect(Collectors.toList());

            log.info("成功列出 {} 个文件。", fileUrls.size());
            return fileUrls;

        } catch (OSSException | ClientException e) {
            log.error("列出 OSS 文件时出错。Bucket: '{}', Prefix: '{}'", bucketName, prefix, e);
            throw new RuntimeException("Failed to list objects from OSS.", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 从 OSS 中删除一个指定对象。
     *
     * @param objectName 要删除的对象的完整名称。
     */
    public void deleteObject(String objectName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        log.info("准备从 OSS 删除对象. Bucket: '{}', ObjectName: '{}'", bucketName, objectName);
        try {
            ossClient.deleteObject(bucketName, objectName);
            log.info("对象 '{}' 已成功从 OSS 删除。", objectName);
        } catch (OSSException | ClientException e) {
            log.error("删除 OSS 对象 '{}' 时出错。", objectName, e);
            throw new RuntimeException("Failed to delete object from OSS.", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}