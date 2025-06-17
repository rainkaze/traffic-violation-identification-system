package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;

@Data
public class WorkflowListDto {
    private Integer workflowId;
    private String workflowName;
    private String description;
    private boolean isActive;
    private String createdByFullName; // 创建人姓名
    private int nodeCount; // 节点数量
}