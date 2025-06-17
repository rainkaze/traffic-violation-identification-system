package edu.cupk.trafficviolationidentificationsystem.model;

import lombok.Data;

@Data
public class Workflow {
    private Integer workflowId;
    private String workflowName;
    private String description;
    private boolean isActive;
    private Integer createdBy;
}