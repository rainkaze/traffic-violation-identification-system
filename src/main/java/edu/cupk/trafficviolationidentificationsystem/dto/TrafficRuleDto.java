package edu.cupk.trafficviolationidentificationsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TrafficRuleDto {
    private Integer ruleId;
    private String violationType;
    private String legalReference;         // 对应 legal_reference
    @NotNull(message = "罚款金额不能为空")
    @Min(value = 0, message = "罚款不能为负数")
    private BigDecimal baseFine;          // 对应 base_fine，使用 BigDecimal 更准确
    private Integer baseDemeritPoints;    // 对应 base_demerit_points
}
