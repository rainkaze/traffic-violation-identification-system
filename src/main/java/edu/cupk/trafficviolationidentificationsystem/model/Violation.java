package edu.cupk.trafficviolationidentificationsystem.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 对应于数据库中 'violations' 表的实体类
 */
@Data
public class Violation {

    /**
     * 违法记录唯一标识符, 主键
     * 对应数据库字段: violation_id
     */
    private Long violationId;

    /**
     * 违法车辆的车牌号
     * 对应数据库字段: plate_number
     */
    private String plateNumber;

    /**
     * 违法行为发生的确切时间
     * 对应数据库字段: violation_time
     */
    private LocalDateTime violationTime;

    /**
     * 捕获该违法的设备ID, 外键
     * 对应数据库字段: device_id
     */
    private Integer deviceId;

    /**
     * 对应的交通法规ID, 外键
     * 对应数据库字段: rule_id
     */
    private Integer ruleId;

    /**
     * 存储证据图片的URL列表
     * 对应数据库字段: evidence_image_urls (JSON)
     */
    private List<String> evidenceImageUrls;

    /**
     * AI识别车牌的置信度得分
     * 对应数据库字段: confidence_score
     */
    private BigDecimal confidenceScore;

    /**
     * 违法记录的宏观生命周期状态
     * 对应数据库字段: status (ENUM)
     */
    private String status;

    /**
     * 记录创建时间
     * 对应数据库字段: created_at
     */
    private LocalDateTime createdAt;
}