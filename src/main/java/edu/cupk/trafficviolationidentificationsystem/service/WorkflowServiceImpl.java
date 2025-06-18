package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.*;
import edu.cupk.trafficviolationidentificationsystem.model.*;
import edu.cupk.trafficviolationidentificationsystem.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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
    public WorkflowDetailDto getWorkflowDetailById(Integer workflowId) {
        Workflow workflow = workflowMapper.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("工作流不存在"));

        WorkflowDetailDto detailDto = new WorkflowDetailDto();
        detailDto.setWorkflowId(workflow.getWorkflowId());
        detailDto.setWorkflowName(workflow.getWorkflowName());
        detailDto.setDescription(workflow.getDescription());
        detailDto.setActive(workflow.isActive());

        // 查找触发器
        WorkflowTrigger trigger = workflowTriggerMapper.findByWorkflowId(workflowId);
        if (trigger != null) {
            WorkflowTriggerUpsertDto triggerDto = new WorkflowTriggerUpsertDto();
            triggerDto.setDistrictId(trigger.getDistrictId());
            triggerDto.setRuleId(trigger.getRuleId());
            triggerDto.setMinDemeritPoints(trigger.getMinDemeritPoints());
            triggerDto.setMaxDemeritPoints(trigger.getMaxDemeritPoints());
            triggerDto.setPriority(trigger.getPriority());
            detailDto.setTrigger(triggerDto);
        }

        // 查找节点及分配
        List<WorkflowNode> nodes = workflowNodeMapper.findNodesByWorkflowId(workflowId);
        List<WorkflowNodeUpsertDto> nodeDtos = nodes.stream().map(node -> {
            WorkflowNodeUpsertDto nodeDto = new WorkflowNodeUpsertDto();
            nodeDto.setNodeName(node.getNodeName());
            nodeDto.setStepOrder(node.getStepOrder());
            nodeDto.setCompletionRule(node.getCompletionRule());

            WorkflowNodeAssignment assignment = workflowNodeAssignmentMapper.findByNodeId(node.getNodeId());
            if (assignment != null) {
                nodeDto.setAssignmentType(assignment.getAssignmentType());
                if ("DYNAMIC_ROLE_IN_DISTRICT".equals(assignment.getAssignmentType())) {
                    nodeDto.setAssignedRank(assignment.getAssignedRank());
                } else if ("STATIC_USER_LIST".equals(assignment.getAssignmentType())) {
                    List<Integer> userIds = workflowNodeAssignmentMapper.findStaticUserIdsByAssignmentId(assignment.getAssignmentId());
                    nodeDto.setStaticUserIds(userIds);
                }
            }
            return nodeDto;
        }).collect(Collectors.toList());
        detailDto.setNodes(nodeDtos);

        return detailDto;
    }

    @Override
    @Transactional
    public Workflow updateWorkflow(Integer workflowId, WorkflowUpsertDto dto) {
        Workflow workflow = workflowMapper.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("工作流不存在"));

        // 更新主表信息
        workflow.setWorkflowName(dto.getWorkflowName());
        workflow.setDescription(dto.getDescription());
        workflow.setActive(dto.isActive());
        workflowMapper.updateWorkflow(workflow);

        // 删除旧的触发器和节点信息
        workflowTriggerMapper.deleteByWorkflowId(workflowId);
        workflowNodeMapper.deleteByWorkflowId(workflowId); // 节点删除会级联删除分配规则

        // 使用通用方法保存新的详情
        saveWorkflowDetails(workflowId, dto);

        return workflow;
    }

    private void saveWorkflowDetails(Integer workflowId, WorkflowUpsertDto dto) {
        // 创建触发器
        WorkflowTrigger trigger = new WorkflowTrigger();
        trigger.setWorkflowId(workflowId);
        trigger.setDistrictId(dto.getTrigger().getDistrictId());
        trigger.setRuleId(dto.getTrigger().getRuleId());
        trigger.setMinDemeritPoints(dto.getTrigger().getMinDemeritPoints());
        trigger.setMaxDemeritPoints(dto.getTrigger().getMaxDemeritPoints());
        trigger.setPriority(dto.getTrigger().getPriority());
        workflowTriggerMapper.insertTrigger(trigger);

        // 创建节点和分配
        for (WorkflowNodeUpsertDto nodeDto : dto.getNodes()) {
            WorkflowNode node = new WorkflowNode();
            node.setWorkflowId(workflowId);
            node.setNodeName(nodeDto.getNodeName());
            node.setStepOrder(nodeDto.getStepOrder());
            node.setCompletionRule(nodeDto.getCompletionRule());
            workflowNodeMapper.insertNode(node);

            WorkflowNodeAssignment assignment = new WorkflowNodeAssignment();
            assignment.setNodeId(node.getNodeId());
            assignment.setAssignmentType(nodeDto.getAssignmentType());
            if ("DYNAMIC_ROLE_IN_DISTRICT".equals(nodeDto.getAssignmentType())) {
                assignment.setAssignedRank(nodeDto.getAssignedRank());
            }
            workflowNodeAssignmentMapper.insertAssignment(assignment);

            if ("STATIC_USER_LIST".equals(nodeDto.getAssignmentType()) && nodeDto.getStaticUserIds() != null) {
                for (Integer userId : nodeDto.getStaticUserIds()) {
                    workflowNodeAssignmentMapper.insertStaticUser(assignment.getAssignmentId(), userId);
                }
            }
        }
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