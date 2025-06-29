package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationQueryDto;
import edu.cupk.trafficviolationidentificationsystem.entity.WarningRule;
import edu.cupk.trafficviolationidentificationsystem.repository.NotificationSettingMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationWarningMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.WarningRuleMapper;
import edu.cupk.trafficviolationidentificationsystem.service.ViolationService;
import edu.cupk.trafficviolationidentificationsystem.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

/**
 * 违法记录管理控制器。
 * 提供违法记录的查询、筛选、分页和导出功能。
 */
@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    private final ViolationService violationService;

    @Autowired
    private WarningRuleMapper ruleMapper;
    @Autowired
    private  WarningRuleMapper ruleMapper;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private NotificationSettingMapper notificationSettingMapper;
    private ViolationWarningMapper warningMapper;

    /**
     * 构造函数，注入违法记录服务。
     *
     * @param violationService 违法记录服务实例。
     */
    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    /**
     * 根据查询条件分页获取违法记录列表。
     *
     * @param queryDto 包含筛选条件（如日期、状态、区域等）和分页信息的数据传输对象。
     * @return 封装了分页数据和总条目数的结果对象。
     */
    @GetMapping
    public ResponseEntity<PageResultDto<ViolationDetailDto>> getViolations(ViolationQueryDto queryDto) {
        PageResultDto<ViolationDetailDto> result = violationService.listViolations(queryDto);


//        //后续插入违法数据的时候 直接使用下文内容 就可以将一级预警内容通知给所需的人
//        Violation violation = Violation.builder()
//                .plateNumber("测A-99999")
//                .violationTime(LocalDateTime.now())
//                .deviceId(107)
//                .ruleId(2)
////                .evidenceImageUrls(List.of("img1.jpg", "img2.jpg"))  // 如果有多图
//                .build();
//        violationMapper.insertTestViolation(violation); // 插入并自动回填 violationId
//        ViolationDetailDto detail = violationMapper.findViolationDetailById(violation.getViolationId());
//        System.out.println("插入成功，详细信息如下：");
//        System.out.println(detail);
//// 3. 模拟置信度（或从模型/识别模块中获取）
//        double confidence = 0.91; // 示例置信度，实际应由算法输出
//// 4. 调用判断预警逻辑
//        evaluateAndInsertWarning(detail.getId(), detail.getType(), confidence);


        return ResponseEntity.ok(result);
    }

    /**
     * 根据查询条件导出违法记录。
     * 支持导出为 PDF, XLSX, 和 CSV 格式。
     *
     * @param queryDto 查询条件，与getViolations接口一致。
     * @param format   导出的文件格式 ("pdf", "xlsx", "csv")。
     * @return 包含导出文件数据的字节数组响应实体。
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportViolations(ViolationQueryDto queryDto, @RequestParam String format) {
        try {
            byte[] data = violationService.exportViolations(queryDto, format);
            HttpHeaders headers = new HttpHeaders();
            String filename = "violations." + format.toLowerCase();
            headers.setContentDispositionFormData("attachment", filename);

            switch (format.toLowerCase()) {
                case "pdf" -> headers.setContentType(MediaType.APPLICATION_PDF);
                case "xlsx" -> headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                case "csv" -> headers.setContentType(MediaType.TEXT_PLAIN);
                default -> {
                    return ResponseEntity.badRequest().build();
                }
            }
            return ResponseEntity.ok().headers(headers).body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * [内部逻辑] 根据违法行为评估并插入预警记录。
     * 注意：此方法会执行数据库写操作。
     *
     * @param violationId     违法记录ID
     * @param violationType   违法类型
     * @param confidenceScore 置信度分数
     */
    private void evaluateAndInsertWarning(Long violationId, String violationType, Double confidenceScore) {
        List<WarningRule> rules = ruleMapper.findAll();
        rules.sort(Comparator.comparingInt(this::getLevelWeight));

        for (WarningRule rule : rules) {
            if (violationType.contains(rule.getViolationType()) && confidenceScore >= rule.getMinConfidence()) {
                ViolationWarning warning = new ViolationWarning();
                warning.setViolationId(violationId);
                warning.setWarningLevel(rule.getLevel());
                warning.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                warningMapper.insert(warning);
                break; // 只触发最高优先级的预警
            }
        }
    }


    // 用于排序预警等级：一级 < 二级 < 三级
    /**
     * [内部逻辑] 为预警等级提供排序权重。
     *
     * @param rule 预警规则
     * @return 权重值（数字越小，优先级越高）
     */
    private int getLevelWeight(WarningRule rule) {
        return switch (rule.getLevel()) {
            case "一级预警" -> 1;
            case "二级预警" -> 2;
            case "三级预警" -> 3;
            default -> 99;
        };
    }
}