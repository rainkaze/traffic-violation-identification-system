package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用于RabbitMQ消息传递的DTO，封装了违章数据和证据图片内容。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViolationTestDto implements Serializable {
    private String plateNumber;
    private LocalDateTime violationTime;
    private Integer deviceId;
    private Integer ruleId;
    private Double confidenceScore;

}