package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class WorkflowNodeUpsertDto {
    private String nodeName;
    private int stepOrder;
    private String completionRule; // "ANY_ASSIGNEE" or "ALL_ASSIGNEES"
    private String assignmentType; // "DYNAMIC_ROLE_IN_DISTRICT" or "STATIC_USER_LIST"
    private String assignedRank; // if dynamic
    private List<Integer> staticUserIds; // if static
}