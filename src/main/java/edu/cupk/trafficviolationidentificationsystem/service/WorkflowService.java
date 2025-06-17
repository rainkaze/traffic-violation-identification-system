package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.WorkflowCreateDto;
import edu.cupk.trafficviolationidentificationsystem.model.Workflow;

public interface WorkflowService {
    Workflow createWorkflow(WorkflowCreateDto workflowCreateDto);
}