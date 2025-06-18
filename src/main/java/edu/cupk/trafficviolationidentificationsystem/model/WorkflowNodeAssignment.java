package edu.cupk.trafficviolationidentificationsystem.model;
import lombok.Data;

@Data
public class WorkflowNodeAssignment {
    private Integer assignmentId;
    private Integer nodeId;
    private String assignmentType;
    private String assignedRank;
}