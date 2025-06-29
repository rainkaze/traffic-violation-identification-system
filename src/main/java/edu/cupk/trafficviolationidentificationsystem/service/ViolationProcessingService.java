package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationProcessingDetailDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.model.ViolationProcessingLog;
import edu.cupk.trafficviolationidentificationsystem.model.WorkflowNode;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationProcessingLogMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.WorkflowNodeMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ViolationProcessingService {

    private final ViolationMapper violationMapper;
    private final ViolationProcessingLogMapper logMapper;
    private final WorkflowNodeMapper nodeMapper;
    private final UserMapper userMapper;

    public ViolationProcessingService(ViolationMapper violationMapper, ViolationProcessingLogMapper logMapper, WorkflowNodeMapper nodeMapper, UserMapper userMapper) {
        this.violationMapper = violationMapper;
        this.logMapper = logMapper;
        this.nodeMapper = nodeMapper;
        this.userMapper = userMapper;
    }

    public ViolationProcessingDetailDto getProcessingDetails(Long violationId) {
        // 使用新方法直接获取 DTO
        ViolationDetailDto violationDetail = Optional.ofNullable(violationMapper.findViolationDetailById(violationId))
                .orElseThrow(() -> new RuntimeException("未找到ID为 " + violationId + " 的违法记录"));

        ViolationProcessingDetailDto result = new ViolationProcessingDetailDto();
        result.setViolation(violationDetail);

        List<ViolationProcessingLog> logs = logMapper.findByViolationId(violationId);

        if (logs.isEmpty()) {
            result.setWorkflowCase(false);
            result.setCurrentUserAssigned(true); // 单节点默认可处理
            return result;
        }

        result.setWorkflowCase(true);
        ViolationProcessingLog latestLog = logs.get(logs.size() - 1);

        List<WorkflowNode> nodes = nodeMapper.findNodesByWorkflowId(latestLog.getWorkflowId());
        result.setWorkflowNodes(nodes);

        WorkflowNode currentNode = nodes.stream()
                .filter(n -> n.getNodeId().equals(latestLog.getNodeId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("工作流节点数据不一致"));
        result.setCurrentStep(currentNode.getStepOrder());
        result.setCurrentNodeStatus(latestLog.getStatus());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("当前用户不存在"));

        // 修正：从所有待处理日志中检查当前用户是否是处理人
        boolean isAssigned = logs.stream()
                .anyMatch(log -> "待处理".equals(log.getStatus()) && Objects.equals(currentUser.getUserId(), log.getAssignedUserId()));
        result.setCurrentUserAssigned(isAssigned);

        return result;
    }
}