package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.WorkflowListDto;
import edu.cupk.trafficviolationidentificationsystem.dto.WorkflowNodeUpsertDto;
import edu.cupk.trafficviolationidentificationsystem.dto.WorkflowUpsertDto;
import edu.cupk.trafficviolationidentificationsystem.model.*;
import edu.cupk.trafficviolationidentificationsystem.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final UserMapper userMapper;
    private final WorkflowMapper workflowMapper;
    private final WorkflowTriggerMapper workflowTriggerMapper;
    private final WorkflowNodeMapper workflowNodeMapper;
    private final WorkflowNodeAssignmentMapper workflowNodeAssignmentMapper;

    public WorkflowServiceImpl(UserMapper userMapper, WorkflowMapper workflowMapper, WorkflowTriggerMapper workflowTriggerMapper, WorkflowNodeMapper workflowNodeMapper, WorkflowNodeAssignmentMapper workflowNodeAssignmentMapper) {
        this.userMapper = userMapper;
        this.workflowMapper = workflowMapper;
        this.workflowTriggerMapper = workflowTriggerMapper;
        this.workflowNodeMapper = workflowNodeMapper;
        this.workflowNodeAssignmentMapper = workflowNodeAssignmentMapper;
    }

    @Override
    public List<WorkflowListDto> getAllWorkflows() {
        return workflowMapper.findAllForList();
    }
    @Override
    @Transactional
    public void deleteWorkflow(Integer workflowId) {
        workflowMapper.deleteById(workflowId);
    }
    @Override
    @Transactional
    public Workflow createWorkflow(WorkflowUpsertDto dto) {
        User currentUser = userMapper.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("当前用户不存在"));

        // 1. 创建主工作流记录
        Workflow workflow = new Workflow();
        workflow.setWorkflowName(dto.getWorkflowName());
        workflow.setDescription(dto.getDescription());
        workflow.setActive(dto.isActive());
        workflow.setCreatedBy(currentUser.getUserId());
        workflowMapper.insertWorkflow(workflow); // MyBatis 会将自增主键回填到 workflow 对象中

        // 2. 创建触发器记录
        WorkflowTrigger trigger = new WorkflowTrigger();
        trigger.setWorkflowId(workflow.getWorkflowId());
        trigger.setDistrictId(dto.getTrigger().getDistrictId());
        trigger.setRuleId(dto.getTrigger().getRuleId());
        trigger.setMinDemeritPoints(dto.getTrigger().getMinDemeritPoints());
        trigger.setMaxDemeritPoints(dto.getTrigger().getMaxDemeritPoints());
        trigger.setPriority(dto.getTrigger().getPriority());
        workflowTriggerMapper.insertTrigger(trigger);

        // 3. 循环创建节点和节点分配规则
        for (WorkflowNodeUpsertDto nodeDto : dto.getNodes()) {
            // 3a. 创建节点
            WorkflowNode node = new WorkflowNode();
            node.setWorkflowId(workflow.getWorkflowId());
            node.setNodeName(nodeDto.getNodeName());
            node.setStepOrder(nodeDto.getStepOrder());
            node.setCompletionRule(nodeDto.getCompletionRule());
            workflowNodeMapper.insertNode(node); // 获取自增的 nodeId

            // 3b. 创建节点的分配规则
            WorkflowNodeAssignment assignment = new WorkflowNodeAssignment();
            assignment.setNodeId(node.getNodeId());
            assignment.setAssignmentType(nodeDto.getAssignmentType());
            if ("DYNAMIC_ROLE_IN_DISTRICT".equals(nodeDto.getAssignmentType())) {
                assignment.setAssignedRank(nodeDto.getAssignedRank());
            }
            workflowNodeAssignmentMapper.insertAssignment(assignment); // 获取自增的 assignmentId

            // 3c. 如果是静态分配，插入用户列表
            if ("STATIC_USER_LIST".equals(nodeDto.getAssignmentType()) && nodeDto.getStaticUserIds() != null) {
                for (Integer userId : nodeDto.getStaticUserIds()) {
                    workflowNodeAssignmentMapper.insertStaticUser(assignment.getAssignmentId(), userId);
                }
            }
        }

        return workflow;
    }

    @Override
    @Transactional
    public boolean toggleWorkflowActivation(Integer workflowId) {
        Workflow workflow = workflowMapper.findById(workflowId) // 你需要先在Mapper中添加findById方法
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        boolean newStatus = !workflow.isActive();
        workflowMapper.updateActivationStatus(workflowId, newStatus);
        return newStatus;
    }
}