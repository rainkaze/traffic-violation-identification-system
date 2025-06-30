package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationProcessingDetailDto;
import edu.cupk.trafficviolationidentificationsystem.model.DailyReport;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.model.ViolationProcessingLog;
import edu.cupk.trafficviolationidentificationsystem.model.WorkflowNode;
import edu.cupk.trafficviolationidentificationsystem.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
public class ViolationProcessingService {

    private final ViolationMapper violationMapper;
    private final ViolationProcessingLogMapper logMapper;
    private final WorkflowNodeMapper nodeMapper;
    private final UserMapper userMapper;
    private final LeaderboardService leaderboardService;
    private final DailyReportRepository reportRepository; // 新增的字段

    // 修改构造函数，注入新的 Repository
    public ViolationProcessingService(ViolationMapper violationMapper, ViolationProcessingLogMapper logMapper, WorkflowNodeMapper nodeMapper, UserMapper userMapper, LeaderboardService leaderboardService, DailyReportRepository reportRepository) {
        this.violationMapper = violationMapper;
        this.logMapper = logMapper;
        this.nodeMapper = nodeMapper;
        this.userMapper = userMapper;
        this.leaderboardService = leaderboardService;
        this.reportRepository = reportRepository; // 注入
    }
    public void recordProcessingSuccess(Integer userId) {
        User user = userMapper.findById(userId).orElse(null);
        if (user != null) {
            leaderboardService.incrementScore(user.getFullName(), 1);
        }
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
    // =================================================================
    // ============ 新增的方法，供 Quartz 任务调度 ============
    // =================================================================

    /**
     * 【Quartz 任务逻辑】生成前一天的交通违法摘要报告，并存入数据库。
     */
    public void generateDailyViolationReport() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String reportDateStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 1. 模拟统计数据（真实场景会调用 violationMapper）
        // 1. 调用 ViolationMapper 来精确统计昨天的数据
        Integer totalViolations = violationMapper.countViolationsByDate(yesterday);

        // 2. 创建报表对象
        DailyReport report = new DailyReport();
        report.setReportDate(yesterday);
        // 如果当天没有违法记录，totalViolations 可能是 null，我们将其设为 0
        report.setTotalViolations(totalViolations != null ? totalViolations : 0);
        report.setGeneratedBy("Quartz Job"); // 我们可以更新一下生成者信息

        // 3. 将报表存入数据库
        reportRepository.save(report);

        // 4. 在控制台打印日志，表明任务已执行
        System.out.println();
        System.out.println("====== Quartz Job: " + reportDateStr + " 的报表已成功生成！ ======");
        System.out.println("====== 统计结果：总计违法 " + report.getTotalViolations() + " 起。");
        System.out.println();
    }

    /**
     * 【Quartz 任务逻辑】清理系统产生的临时文件或过期数据。
     * 该方法将被 TempFileCleanupJob 调用。
     */
    public void cleanupTemporaryFiles() {
        // 在真实场景中，这里会扫描临时目录（如 /tmp/violation_videos）并删除符合条件的文件
        long releasedSpace = new Random().nextInt(300) + 50; // 模拟释放的空间大小

//        System.out.println();
//        System.out.println("---------------------------------------------------------");
//        System.out.println("-------> Quartz Job: [系统维护] 正在执行临时文件清理任务... <-------");
//        System.out.println("-------> Quartz Job: [系统维护] 清理完成，释放了 " + releasedSpace + "MB 磁盘空间。 <-------");
//        System.out.println("---------------------------------------------------------");
//        System.out.println();
    }
}