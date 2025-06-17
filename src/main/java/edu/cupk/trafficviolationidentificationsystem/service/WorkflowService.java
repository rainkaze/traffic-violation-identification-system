package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.WorkflowListDto;
import edu.cupk.trafficviolationidentificationsystem.dto.WorkflowUpsertDto;
import edu.cupk.trafficviolationidentificationsystem.model.Workflow;
import java.util.List;

public interface WorkflowService {
    List<WorkflowListDto> getAllWorkflows();
    Workflow createWorkflow(WorkflowUpsertDto workflowCreateDto);
    void deleteWorkflow(Integer workflowId);
    boolean toggleWorkflowActivation(Integer workflowId);

}