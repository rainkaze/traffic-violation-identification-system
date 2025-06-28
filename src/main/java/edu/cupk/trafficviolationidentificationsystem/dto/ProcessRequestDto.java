package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;

@Data
public class ProcessRequestDto {
    private String decision; // "APPROVE" 或 "REJECT"
    private String remarks; // 备注
    private boolean isWorkflowCase; // 新增字段，标记是否为工作流处理
}