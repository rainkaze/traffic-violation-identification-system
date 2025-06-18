package edu.cupk.trafficviolationidentificationsystem.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WorkflowListDto {
    private Integer workflowId;
    private String workflowName;
    private String description;

    @JsonProperty("isActive")
    private boolean isActive;

    private String createdByFullName; // 创建人姓名
    private int nodeCount; // 节点数量
}