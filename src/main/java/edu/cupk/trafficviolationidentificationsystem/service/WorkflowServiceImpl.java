package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.WorkflowCreateDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.model.Workflow;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.WorkflowMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowMapper workflowMapper;
    private final UserMapper userMapper;

    public WorkflowServiceImpl(WorkflowMapper workflowMapper, UserMapper userMapper) {
        this.workflowMapper = workflowMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public Workflow createWorkflow(WorkflowCreateDto workflowCreateDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("当前用户不存在"));

        Workflow workflow = new Workflow();
        workflow.setWorkflowName(workflowCreateDto.getWorkflowName());
        workflow.setDescription(workflowCreateDto.getDescription());
        workflow.setActive(workflowCreateDto.isActive());
        workflow.setCreatedBy(currentUser.getUserId());

        workflowMapper.insertWorkflow(workflow);
        return workflow;
    }
}