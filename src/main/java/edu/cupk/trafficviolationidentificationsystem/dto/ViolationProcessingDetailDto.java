package edu.cupk.trafficviolationidentificationsystem.dto;

import edu.cupk.trafficviolationidentificationsystem.model.Violation;
import edu.cupk.trafficviolationidentificationsystem.model.WorkflowNode;
import lombok.Data;
import java.util.List;

@Data
public class ViolationProcessingDetailDto {
    private ViolationDetailDto violation;
    private List<WorkflowNode> workflowNodes;
    private Integer currentStep;
    private String currentNodeStatus;
    // 修正字段名以生成标准的 setter: setCurrentUserAssigned
    private boolean currentUserAssigned;
    private boolean workflowCase;
}