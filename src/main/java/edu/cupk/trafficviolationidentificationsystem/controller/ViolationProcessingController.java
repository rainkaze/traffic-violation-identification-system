package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.ProcessRequestDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationProcessingDetailDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.model.Workflow;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.service.LeaderboardService; // 确保已导入
import edu.cupk.trafficviolationidentificationsystem.service.ViolationProcessingService;
import edu.cupk.trafficviolationidentificationsystem.service.WorkflowEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/processing")
public class ViolationProcessingController {

    private final WorkflowEngineService workflowEngineService;
    private final ViolationProcessingService violationProcessingService;
    private final UserMapper userMapper;
    private final LeaderboardService leaderboardService; // 确保 LeaderboardService 已注入

    // 构造函数保持不变
    public ViolationProcessingController(WorkflowEngineService workflowEngineService, ViolationProcessingService violationProcessingService, UserMapper userMapper, LeaderboardService leaderboardService) {
        this.workflowEngineService = workflowEngineService;
        this.violationProcessingService = violationProcessingService;
        this.userMapper = userMapper;
        this.leaderboardService = leaderboardService;
    }

    // initiateProcessing 和 getProcessingDetails 方法保持不变
    @PostMapping("/initiate/{id}")
    public ResponseEntity<?> initiateProcessing(@PathVariable("id") Long violationId) {
        Workflow startedWorkflow = workflowEngineService.startWorkflowForViolation(violationId);
        boolean isWorkflowCase = startedWorkflow != null;
        return ResponseEntity.ok(Map.of("isWorkflowCase", isWorkflowCase));
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ViolationProcessingDetailDto> getProcessingDetails(@PathVariable("id") Long violationId) {
        ViolationProcessingDetailDto details = violationProcessingService.getProcessingDetails(violationId);
        return ResponseEntity.ok(details);
    }

    // ======== 主要修改点在此处 ========
    @PostMapping("/submit/{id}")
    public ResponseEntity<?> submitDecision(@PathVariable("id") Long violationId, @RequestBody ProcessRequestDto processRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userMapper.findByUsername(username).orElseThrow();

        try {
            // 这里我们区分是工作流提交还是直接提交
            if(processRequest.isWorkflowCase()) {
                workflowEngineService.processNode(violationId, currentUser.getUserId(), processRequest.getDecision(), processRequest.getRemarks());
            } else {
                workflowEngineService.processDirectly(violationId, currentUser.getUserId(), processRequest.getRemarks());
            }

            // **新增代码**: 在处理成功后，为当前警员增加排行榜分数
            leaderboardService.incrementScore(currentUser.getFullName(), 1);

            return ResponseEntity.ok(Map.of("message", "处理成功！"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}