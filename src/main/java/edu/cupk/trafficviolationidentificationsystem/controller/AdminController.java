package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.*;
import edu.cupk.trafficviolationidentificationsystem.model.Workflow;
import edu.cupk.trafficviolationidentificationsystem.service.AdminService;
import edu.cupk.trafficviolationidentificationsystem.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('管理员')") // 确保只有管理员能访问
public class AdminController {

    private final AdminService adminService;
    private final WorkflowService workflowService; // 注入新Service


    public AdminController(AdminService adminService, WorkflowService workflowService) {
        this.adminService = adminService;
        this.workflowService = workflowService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/users/{userId}/approve")
    public ResponseEntity<Void> approveUser(@PathVariable Integer userId) {
        adminService.approveUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/reject")
    public ResponseEntity<Void> rejectUser(@PathVariable Integer userId) {
        adminService.rejectUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserUpsertDto userDto) {
        UserDto createdUser = adminService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer userId, @Valid @RequestBody UserUpsertDto userDto) {
        UserDto updatedUser = adminService.updateUser(userId, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{userId}/districts")
    public ResponseEntity<Void> updateUserDistricts(@PathVariable Integer userId, @RequestBody UserDistrictAssignmentDto assignmentDto) {
        adminService.updateUserDistricts(userId, assignmentDto.getDistrictIds());
        return ResponseEntity.ok().build();
    }

    // 新增工作流创建端点
    /**
     * 获取所有工作流的列表.
     * 这个方法调用 service 层，而不是 mapper.
     */
    @GetMapping("/workflows")
    public ResponseEntity<List<WorkflowListDto>> getAllWorkflows() {
        return ResponseEntity.ok(workflowService.getAllWorkflows());
    }

    /**
     * 创建一个新的工作流.
     * 注意：这里的请求体是 WorkflowUpsertDto，以匹配我们前端的复杂表单.
     */
    @PostMapping("/workflows")
    public ResponseEntity<Workflow> createWorkflow(@Valid @RequestBody WorkflowUpsertDto workflowUpsertDto) {
        Workflow createdWorkflow = workflowService.createWorkflow(workflowUpsertDto);
        return new ResponseEntity<>(createdWorkflow, HttpStatus.CREATED);
    }
    @GetMapping("/workflows/{id}")
    public ResponseEntity<WorkflowDetailDto> getWorkflowById(@PathVariable Integer id) {
        WorkflowDetailDto workflowDetail = workflowService.getWorkflowDetailById(id);
        return ResponseEntity.ok(workflowDetail);
    }

    @PutMapping("/workflows/{id}")
    public ResponseEntity<Workflow> updateWorkflow(@PathVariable Integer id, @Valid @RequestBody WorkflowUpsertDto workflowUpsertDto) {
        Workflow updatedWorkflow = workflowService.updateWorkflow(id, workflowUpsertDto);
        return ResponseEntity.ok(updatedWorkflow);
    }


    @DeleteMapping("/workflows/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable("id") Integer workflowId) {
        workflowService.deleteWorkflow(workflowId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/for-assignment")
    public ResponseEntity<List<UserForAssignmentDto>> getUsersForAssignment(@RequestParam(required = false) Integer districtId) {
        return ResponseEntity.ok(adminService.getUsersForAssignment(districtId));
    }
    @PostMapping("/workflows/{id}/toggle-activation")
    public ResponseEntity<Boolean> toggleActivation(@PathVariable Integer id) {
        boolean newStatus = workflowService.toggleWorkflowActivation(id);
        return ResponseEntity.ok(newStatus);
    }
}