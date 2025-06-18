package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class WorkflowUpsertDto {
    private String workflowName;
    private String description;
    private boolean isActive;
    private WorkflowTriggerUpsertDto trigger;
    private List<WorkflowNodeUpsertDto> nodes;
}