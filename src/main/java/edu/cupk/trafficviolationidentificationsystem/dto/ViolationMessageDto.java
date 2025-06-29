package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 用于RabbitMQ消息传递的DTO，封装了违章数据和证据图片内容。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViolationMessageDto implements Serializable {
    // 确保 ViolationTestDto 也实现了 Serializable 接口
    private ViolationTestDto violationData;

    private byte[] imageBytes;
    private String originalImageName;
    private String imageContentType;
}