package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.TrafficRuleDto;
import edu.cupk.trafficviolationidentificationsystem.model.*;
import edu.cupk.trafficviolationidentificationsystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class WorkflowEngineService {

    private final ViolationMapper violationMapper;
    private final DeviceMapper deviceMapper;
    private final TrafficRuleMapper trafficRuleMapper;
    private final WorkflowTriggerMapper workflowTriggerMapper;
    private final WorkflowMapper workflowMapper;
    private final WorkflowNodeMapper workflowNodeMapper;
    private final WorkflowNodeAssignmentMapper workflowNodeAssignmentMapper;
    private final UserMapper userMapper;
    private final ViolationProcessingLogMapper logMapper;
    private final EnforcementCaseMapper caseMapper;

    // 使用构造函数注入所有 Mapper
    public WorkflowEngineService(ViolationMapper violationMapper, DeviceMapper deviceMapper, TrafficRuleMapper trafficRuleMapper, WorkflowTriggerMapper workflowTriggerMapper, WorkflowMapper workflowMapper, WorkflowNodeMapper workflowNodeMapper, WorkflowNodeAssignmentMapper workflowNodeAssignmentMapper, UserMapper userMapper, ViolationProcessingLogMapper logMapper, EnforcementCaseMapper caseMapper) {
        this.violationMapper = violationMapper;
        this.deviceMapper = deviceMapper;
        this.trafficRuleMapper = trafficRuleMapper;
        this.workflowTriggerMapper = workflowTriggerMapper;
        this.workflowMapper = workflowMapper;
        this.workflowNodeMapper = workflowNodeMapper;
        this.workflowNodeAssignmentMapper = workflowNodeAssignmentMapper;
        this.userMapper = userMapper;
        this.logMapper = logMapper;
        this.caseMapper = caseMapper;
    }

    @Transactional
    public Workflow startWorkflowForViolation(Long violationId) {
        Violation violation = violationMapper.findById(violationId);
        if (violation == null || !"待处理".equals(violation.getStatus())) {
            return null;
        }

        Device device = deviceMapper.findById(violation.getDeviceId()).orElse(null);
        if (device == null) return null;

        TrafficRuleDto rule = trafficRuleMapper.findAllRules().stream()
                .filter(r -> r.getRuleId().equals(violation.getRuleId()))
                .findFirst()
                .orElse(null);
        if (rule == null) return null;

        // 完善触发器匹配逻辑
        int demeritPoints = rule.getBaseDemeritPoints();
        List<WorkflowTrigger> matchedTriggers = workflowTriggerMapper.findMatchingTriggers(device.getDistrictId(), violation.getRuleId(), demeritPoints);

        Workflow workflowToStart = matchedTriggers.stream()
                .map(trigger -> workflowMapper.findById(trigger.getWorkflowId()).orElse(null))
                .filter(Objects::nonNull)
                .filter(Workflow::isActive)
                .findFirst()
                .orElse(null);

        if (workflowToStart != null) {
            violationMapper.updateStatus(violationId, "处理中");
            advanceToNextNode(violation, workflowToStart, null, null); // 首次进入，没有前置节点和处理人
        }
        return workflowToStart;
    }


    @Transactional
    public void processNode(Long violationId, Integer currentUserId, String decision, String remarks) {
        ViolationProcessingLog currentTask = logMapper.findByViolationId(violationId).stream()
                .filter(log -> log.getAssignedUserId().equals(currentUserId) && "待处理".equals(log.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("您没有待处理的任务。"));

        currentTask.setStatus("APPROVE".equals(decision) ? "已完成" : "已驳回");
        currentTask.setRemarks(remarks);
        currentTask.setProcessedAt(java.time.LocalDateTime.now());
        logMapper.update(currentTask);

        if("已驳回".equals(currentTask.getStatus())) {
            violationMapper.updateStatus(violationId, "已处理");
            // 可以在此处创建一条“已作废”的执法记录
            return;
        }

        Workflow workflow = workflowMapper.findById(currentTask.getWorkflowId()).orElseThrow();
        WorkflowNode currentNode = workflowNodeMapper.findNodesByWorkflowId(workflow.getWorkflowId())
                .stream().filter(n -> n.getNodeId().equals(currentTask.getNodeId())).findFirst().orElseThrow();

        if (isNodeCompleted(violationId, currentNode)) {
            advanceToNextNode(violationMapper.findById(violationId), workflow, currentNode, currentUserId);
        }
    }

    private void advanceToNextNode(Violation violation, Workflow workflow, WorkflowNode previousNode, Integer processorId) {
        int nextStepOrder = (previousNode == null) ? 1 : previousNode.getStepOrder() + 1;

        WorkflowNode nextNode = workflowNodeMapper.findNodesByWorkflowId(workflow.getWorkflowId())
                .stream()
                .filter(n -> n.getStepOrder() == nextStepOrder)
                .findFirst()
                .orElse(null);

        if (nextNode != null) {
            List<Integer> assignees = getAssigneesForNode(violation, nextNode);
            assignees.forEach(assigneeId -> {
                ViolationProcessingLog newLog = new ViolationProcessingLog();
                newLog.setViolationId(violation.getViolationId());
                newLog.setWorkflowId(workflow.getWorkflowId());
                newLog.setNodeId(nextNode.getNodeId());
                newLog.setAssignedUserId(assigneeId);
                newLog.setStatus("待处理");
                logMapper.insert(newLog);
            });
        } else {
            completeWorkflow(violation, processorId);
        }
    }

    private boolean isNodeCompleted(Long violationId, WorkflowNode node) {
        if ("ANY_ASSIGNEE".equals(node.getCompletionRule())) {
            return true;
        }

        List<Integer> totalAssignees = getAssigneesForNode(violationMapper.findById(violationId), node);
        int completedCount = logMapper.countCompletedAssignees(violationId, node.getNodeId());

        return completedCount >= totalAssignees.size();
    }

    private List<Integer> getAssigneesForNode(Violation violation, WorkflowNode node) {
        WorkflowNodeAssignment assignment = workflowNodeAssignmentMapper.findByNodeId(node.getNodeId());
        if ("STATIC_USER_LIST".equals(assignment.getAssignmentType())) {
            return workflowNodeAssignmentMapper.findStaticUserIdsByAssignmentId(assignment.getAssignmentId());
        } else { // DYNAMIC_ROLE_IN_DISTRICT
            Device device = deviceMapper.findById(violation.getDeviceId()).orElseThrow();
            return userMapper.findUsersForAssignment(device.getDistrictId()).stream()
                    .filter(u -> u.getRank().equals(assignment.getAssignedRank()))
                    .map(User::getUserId)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public void completeWorkflow(Violation violation, Integer finalProcessorId) {
        violationMapper.updateStatus(violation.getViolationId(), "已处理");

        EnforcementCase finalCase = new EnforcementCase();
        finalCase.setViolationId(violation.getViolationId());
        finalCase.setFinalDecision("处罚确认");
        finalCase.setDecisionReason("经工作流审批完成。");
        // 修正：使用传入的 finalProcessorId
        finalCase.setProcessedByUserId(finalProcessorId);

        // 示例：此处可以加入逻辑来设置最终罚款和扣分
        TrafficRuleDto rule = trafficRuleMapper.findAllRules().stream()
                .filter(r -> r.getRuleId().equals(violation.getRuleId())).findFirst().orElse(null);
        if(rule != null) {
            //finalCase.setFinalFine(rule.getBaseFine());
            finalCase.setFinalDemeritPoints(rule.getBaseDemeritPoints());
        }

        caseMapper.insert(finalCase);
    }

    @Transactional
    public void processDirectly(Long violationId, Integer processorId, String remarks) {
        violationMapper.updateStatus(violationId, "已处理");
        EnforcementCase finalCase = new EnforcementCase();
        finalCase.setViolationId(violationId);
        finalCase.setFinalDecision("处罚确认");
        finalCase.setDecisionReason("单节点直接处理: " + remarks);
        finalCase.setProcessedByUserId(processorId);
        caseMapper.insert(finalCase);
    }
}