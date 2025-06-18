package edu.cupk.trafficviolationidentificationsystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class WorkflowUpsertDto {
    private String workflowName;
    private String description;
    @JsonProperty("isActive")
    private boolean isActive;
    private WorkflowTriggerUpsertDto trigger;
    private List<WorkflowNodeUpsertDto> nodes;
}