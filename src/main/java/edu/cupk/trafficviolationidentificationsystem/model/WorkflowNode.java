package edu.cupk.trafficviolationidentificationsystem.model;
import lombok.Data;

@Data
public class WorkflowNode {
    private Integer nodeId;
    private Integer workflowId;
    private String nodeName;
    private int stepOrder;
    private String completionRule;
    private String description;
}