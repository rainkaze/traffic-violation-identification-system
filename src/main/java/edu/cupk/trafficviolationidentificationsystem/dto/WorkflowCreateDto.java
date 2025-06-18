package edu.cupk.trafficviolationidentificationsystem.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WorkflowCreateDto {
    @NotEmpty(message = "工作流名称不能为空")
    @Size(max = 100, message = "工作流名称不能超过100个字符")
    private String workflowName;

    @Size(max = 65535, message = "描述过长")
    private String description;

    private boolean isActive;
}