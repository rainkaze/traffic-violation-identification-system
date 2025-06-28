package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationQueryDto;
import edu.cupk.trafficviolationidentificationsystem.entity.ViolationWarning;
import edu.cupk.trafficviolationidentificationsystem.entity.WarningRule;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationWarningMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.WarningRuleMapper;
import edu.cupk.trafficviolationidentificationsystem.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    private final ViolationService violationService;

    @Autowired
    private  ViolationMapper violationMapper;
    @Autowired
    private  WarningRuleMapper ruleMapper;
    @Autowired
    private  ViolationWarningMapper warningMapper;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    /**
     * 获取违法记录列表（支持筛选和分页）
     * @param queryDto Spring Boot 会自动将请求参数绑定到 DTO 对象上
     * @return 分页后的违法记录数据
     */
    @GetMapping
    public ResponseEntity<PageResultDto<ViolationDetailDto>> getViolations(ViolationQueryDto queryDto) {
        PageResultDto<ViolationDetailDto> result = violationService.listViolations(queryDto);

//        System.out.println("要插入了");

//        //测试是否能够正确插入数据库
//        evaluateAndInsertWarning(101L, "闯红灯111", 0.85); // 应触发一级
//        evaluateAndInsertWarning(103L, "逆行", 0.70);   // 不触发
//        evaluateAndInsertWarning(104L, "逆行", 0.95);   // 应触发一级
//        evaluateAndInsertWarning(105L, "超速", 0.40);   // 不触发

        return ResponseEntity.ok(result);
    }



    private void evaluateAndInsertWarning(Long violationId, String violationType, Double confidenceScore) {
        // 获取所有预警规则
        List<WarningRule> rules = ruleMapper.findAll();

        // 按预警等级排序（一级优先），确保只触发最高级别
        rules.sort(Comparator.comparingInt(this::getLevelWeight));

        // 遍历所有规则，找到匹配的规则
        for (WarningRule rule : rules) {
            if (violationType.contains(rule.getViolationType())
                    && confidenceScore >= rule.getMinConfidence()) {
                ViolationWarning warning = new ViolationWarning();
                warning.setViolationId(violationId);
                warning.setWarningLevel(rule.getLevel());
                warning.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                warningMapper.insert(warning); // 插入预警记录
                break; // 只插入一次，优先级最高的那个
            }
        }
    }

    // 用于排序预警等级：一级 < 二级 < 三级
    private int getLevelWeight(WarningRule rule) {
        return switch (rule.getLevel()) {
            case "一级预警" -> 1;
            case "二级预警" -> 2;
            case "三级预警" -> 3;
            default -> 99;
        };
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportViolations(ViolationQueryDto queryDto, @RequestParam String format) {
        try {
            byte[] data = violationService.exportViolations(queryDto, format);
            HttpHeaders headers = new HttpHeaders();
            String filename = "violations." + format;
            headers.setContentDispositionFormData("attachment", filename);

            if ("pdf".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.APPLICATION_PDF);
            } else if ("xlsx".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            } else if ("csv".equalsIgnoreCase(format)) {
                headers.setContentType(MediaType.TEXT_PLAIN);
            }

            return ResponseEntity.ok().headers(headers).body(data);
        } catch (Exception e) {
            // 在实际应用中，这里应该有更完善的错误处理
            return ResponseEntity.status(500).build();
        }
    }



}