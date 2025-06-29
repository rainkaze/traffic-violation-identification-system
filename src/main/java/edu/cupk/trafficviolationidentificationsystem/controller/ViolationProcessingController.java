package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.dto.ProcessRequestDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationProcessingDetailDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.model.Workflow;
import edu.cupk.trafficviolationidentificationsystem.service.LeaderboardService; // 1. 引入新服务
import edu.cupk.trafficviolationidentificationsystem.service.ViolationProcessingService;
import edu.cupk.trafficviolationidentificationsystem.service.WorkflowEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * [合并后] 违法记录处理流程控制器。
 * 负责启动处理流程、获取处理详情、提交处理决策以及更新排行榜。
 */
@RestController
@RequestMapping("/api/processing")
public class ViolationProcessingController {

    private final WorkflowEngineService workflowEngineService;
    private final ViolationProcessingService violationProcessingService;
    private final LeaderboardService leaderboardService; // 2. 注入新服务

    /**
     * [合并后] 构造函数，注入所需服务。
     *
     * @param workflowEngineService      工作流引擎服务。
     * @param violationProcessingService 违法处理服务。
     * @param leaderboardService         排行榜服务。
     */
    public ViolationProcessingController(WorkflowEngineService workflowEngineService, ViolationProcessingService violationProcessingService, LeaderboardService leaderboardService) {
        this.workflowEngineService = workflowEngineService;
        this.violationProcessingService = violationProcessingService;
        this.leaderboardService = leaderboardService;
    }

    /**
     * 为指定的违法记录启动处理流程。
     * 系统会根据规则判断是进入工作流还是直接处理。
     *
     * @param violationId 要处理的违法记录ID。
     * @return 返回一个Map，其中 "isWorkflowCase" 键指示是否进入了工作流。
     */
    @PostMapping("/initiate/{id}")
    @AuditLog(actionType = "INITIATE_VIOLATION_PROCESSING", targetEntityType = "VIOLATION", targetEntityIdExpression = "#violationId")
    public ResponseEntity<?> initiateProcessing(@PathVariable("id") Long violationId) {
        Workflow startedWorkflow = workflowEngineService.startWorkflowForViolation(violationId);
        boolean isWorkflowCase = startedWorkflow != null;
        return ResponseEntity.ok(Map.of("isWorkflowCase", isWorkflowCase));
    }

    /**
     * 获取指定违法记录的处理详情。
     * 包括违法信息、处理日志、当前状态等。
     *
     * @param violationId 违法记录ID。
     * @return 包含处理详情的DTO。
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<ViolationProcessingDetailDto> getProcessingDetails(@PathVariable("id") Long violationId) {
        ViolationProcessingDetailDto details = violationProcessingService.getProcessingDetails(violationId);
        return ResponseEntity.ok(details);
    }

    /**
     * [合并后] 提交处理决策。
     * 成功处理后，会为处理该任务的用户增加排行榜分数。
     *
     * @param violationId    正在处理的违法记录ID。
     * @param processRequest 包含决策信息（如"pass", "reject"）和备注的请求体。
     * @param authentication Spring Security提供的认证信息，用于获取当前用户。
     * @return 成功或失败的响应信息。
     */
    @PostMapping("/submit/{id}")
    @AuditLog(actionType = "SUBMIT_VIOLATION_DECISION", targetEntityType = "VIOLATION", targetEntityIdExpression = "#violationId")
    public ResponseEntity<?> submitDecision(@PathVariable("id") Long violationId, @RequestBody ProcessRequestDto processRequest, Authentication authentication) {
        // 3. 采用版本1的方式获取用户，更安全简洁
        User currentUser = (User) authentication.getPrincipal();

        try {
            if (processRequest.isWorkflowCase()) {
                workflowEngineService.processNode(violationId, currentUser.getUserId(), processRequest.getDecision(), processRequest.getRemarks());
            } else {
                workflowEngineService.processDirectly(violationId, currentUser.getUserId(), processRequest.getRemarks());
            }

            // 4. 新增功能：处理成功后，增加排行榜分数
            leaderboardService.incrementScore(currentUser.getFullName(), 1);

            return ResponseEntity.ok(Map.of("message", "处理成功！"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}