package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.dto.*;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.model.Workflow;
import edu.cupk.trafficviolationidentificationsystem.service.AdminService;
import edu.cupk.trafficviolationidentificationsystem.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员功能控制器。
 * 提供用户管理、工作流管理等仅限管理员访问的API接口。
 * 所有接口都受 Spring Security 保护，要求用户拥有 '管理员' 角色。
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('管理员')")
public class AdminController {

    private final AdminService adminService;
    private final WorkflowService workflowService;

    /**
     * 构造函数，用于注入所需的服务。
     *
     * @param adminService  管理员服务
     * @param workflowService 工作流服务
     */
    public AdminController(AdminService adminService, WorkflowService workflowService) {
        this.adminService = adminService;
        this.workflowService = workflowService;
    }

    /**
     * 获取所有用户的列表。
     *
     * @return 包含所有用户信息的DTO列表。
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * 批准一个待审核的用户。
     *
     * @param userId 要批准的用户ID。
     * @return 成功时返回HTTP 200 OK。
     */
    @PostMapping("/users/{userId}/approve")
    @AuditLog(actionType = "ADMIN_APPROVE_USER", targetEntityType = "USER", targetEntityIdExpression = "#userId")
    public ResponseEntity<Void> approveUser(@PathVariable Integer userId) {
        adminService.approveUser(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 拒绝一个待审核的用户。
     *
     * @param userId 要拒绝的用户ID。
     * @return 成功时返回HTTP 200 OK。
     */
    @PostMapping("/users/{userId}/reject")
    @AuditLog(actionType = "ADMIN_REJECT_USER", targetEntityType = "USER", targetEntityIdExpression = "#userId")
    public ResponseEntity<Void> rejectUser(@PathVariable Integer userId) {
        adminService.rejectUser(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 管理员创建一个新用户。
     *
     * @param userDto 包含新用户信息的数据传输对象。
     * @return 创建成功后返回新用户的信息及HTTP 201 CREATED状态。
     */
    @PostMapping("/users")
    @AuditLog(actionType = "ADMIN_CREATE_USER", targetEntityType = "USER", targetEntityIdExpression = "#result.body.userId")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserUpsertDto userDto) {
        UserDto createdUser = adminService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * 管理员更新一个已存在用户的信息。
     *
     * @param userId  要更新的用户ID。
     * @param userDto 包含更新后用户信息的数据传输对象。
     * @return 更新成功后返回更新后的用户信息。
     */
    @PutMapping("/users/{userId}")
    @AuditLog(actionType = "ADMIN_UPDATE_USER", targetEntityType = "USER", targetEntityIdExpression = "#userId")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer userId, @Valid @RequestBody UserUpsertDto userDto) {
        UserDto updatedUser = adminService.updateUser(userId, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 管理员删除一个用户。
     *
     * @param userId 要删除的用户ID。
     * @return 成功时返回HTTP 204 No Content。
     */
    @DeleteMapping("/users/{userId}")
    @AuditLog(actionType = "ADMIN_DELETE_USER", targetEntityType = "USER", targetEntityIdExpression = "#userId")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 为用户分配或更新其负责的行政区划。
     *
     * @param userId        要分配的用户ID。
     * @param assignmentDto 包含行政区划ID列表的数据对象。
     * @return 成功时返回HTTP 200 OK。
     */
    @PutMapping("/users/{userId}/districts")
    @AuditLog(actionType = "ASSIGN_DISTRICTS", targetEntityType = "USER", targetEntityIdExpression = "#userId")
    public ResponseEntity<Void> updateUserDistricts(@PathVariable Integer userId, @RequestBody UserDistrictAssignmentDto assignmentDto) {
        adminService.updateUserDistricts(userId, assignmentDto.getDistrictIds());
        return ResponseEntity.ok().build();
    }

    /**
     * 获取所有工作流的简要列表。
     *
     * @return 包含所有工作流信息的DTO列表。
     */
    @GetMapping("/workflows")
    public ResponseEntity<List<WorkflowListDto>> getAllWorkflows() {
        return ResponseEntity.ok(workflowService.getAllWorkflows());
    }

    /**
     * 根据ID获取单个工作流的详细信息。
     *
     * @param id 工作流ID。
     * @return 包含工作流详细信息的DTO。
     */
    @GetMapping("/workflows/{id}")
    public ResponseEntity<WorkflowDetailDto> getWorkflowById(@PathVariable Integer id) {
        WorkflowDetailDto workflowDetail = workflowService.getWorkflowDetailById(id);
        return ResponseEntity.ok(workflowDetail);
    }

    /**
     * 创建一个新的工作流。
     *
     * @param workflowUpsertDto 包含工作流完整定义的数据对象。
     * @return 创建成功后返回新的工作流实体及HTTP 201 CREATED状态。
     */
    @PostMapping("/workflows")
    @AuditLog(actionType = "CREATE_WORKFLOW", targetEntityType = "WORKFLOW", targetEntityIdExpression = "#result.body.workflowId")
    public ResponseEntity<Workflow> createWorkflow(@Valid @RequestBody WorkflowUpsertDto workflowUpsertDto) {
        Workflow createdWorkflow = workflowService.createWorkflow(workflowUpsertDto);
        return new ResponseEntity<>(createdWorkflow, HttpStatus.CREATED);
    }

    /**
     * 更新一个已存在的工作流。
     *
     * @param id                要更新的工作流ID。
     * @param workflowUpsertDto 包含更新后工作流定义的数据对象。
     * @return 更新成功后返回更新后的工作流实体。
     */
    @PutMapping("/workflows/{id}")
    @AuditLog(actionType = "UPDATE_WORKFLOW", targetEntityType = "WORKFLOW", targetEntityIdExpression = "#id")
    public ResponseEntity<Workflow> updateWorkflow(@PathVariable Integer id, @Valid @RequestBody WorkflowUpsertDto workflowUpsertDto) {
        Workflow updatedWorkflow = workflowService.updateWorkflow(id, workflowUpsertDto);
        return ResponseEntity.ok(updatedWorkflow);
    }

    /**
     * 删除一个工作流。
     *
     * @param workflowId 要删除的工作流ID。
     * @return 成功时返回HTTP 204 No Content。
     */
    @DeleteMapping("/workflows/{id}")
    @AuditLog(actionType = "DELETE_WORKFLOW", targetEntityType = "WORKFLOW", targetEntityIdExpression = "#id")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable("id") Integer workflowId) {
        workflowService.deleteWorkflow(workflowId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 切换工作流的激活状态（启用/禁用）。
     *
     * @param id 要切换状态的工作流ID。
     * @return 返回切换后的新状态（true为激活，false为禁用）。
     */
    @PostMapping("/workflows/{id}/toggle-activation")
    @AuditLog(actionType = "TOGGLE_WORKFLOW_ACTIVATION", targetEntityType = "WORKFLOW", targetEntityIdExpression = "#id")
    public ResponseEntity<Boolean> toggleActivation(@PathVariable Integer id) {
        boolean newStatus = workflowService.toggleWorkflowActivation(id);
        return ResponseEntity.ok(newStatus);
    }

    /**
     * 获取可用于分配任务的用户列表。
     *
     * @param districtId 可选的行政区划ID，用于筛选用户。
     * @return 过滤后的用户列表。
     */
    @GetMapping("/users/for-assignment")
    public ResponseEntity<List<UserForAssignmentDto>> getUsersForAssignment(@RequestParam(required = false) Integer districtId) {
        return ResponseEntity.ok(adminService.getUsersForAssignment(districtId));
    }
}