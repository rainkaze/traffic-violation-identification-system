package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationQueryDto;
import edu.cupk.trafficviolationidentificationsystem.entity.WarningRule;
import edu.cupk.trafficviolationidentificationsystem.repository.NotificationSettingMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
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

@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    private final ViolationService violationService;

    @Autowired
    private  ViolationMapper violationMapper;
    @Autowired
    private  WarningRuleMapper ruleMapper;
    @Autowired
    private WebSocketServer webSocketServer;
    @Autowired
    private NotificationSettingMapper notificationSettingMapper;

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



    private void evaluateAndInsertWarning(Long violationId, String violationType, Double confidenceScore) {
        List<WarningRule> rules = ruleMapper.findAll();
        rules.sort(Comparator.comparingInt(this::getLevelWeight));

        for (WarningRule rule : rules) {
            if (violationType.contains(rule.getViolationType())
                    && confidenceScore >= rule.getMinConfidence()) {

                // 2. 如果是一级预警，发送 WebSocket 通知
                if ("一级预警".equals(rule.getLevel())) {
                    ViolationDetailDto detail = violationMapper.findViolationDetailById(violationId);
                    if (detail != null) {
                        String message = String.format("""
                            【一级预警通知】
                            车牌号：%s
                            违法类型：%s
                            时间：%s
                            地点：%s
                            状态：%s
                            """,
                                detail.getPlate(),
                                detail.getType(),
                                detail.getTime(),
                                detail.getLocation() != null ? detail.getLocation() : "未知",
                                detail.getStatus());

                        // 通知的用户 ID 列表，这里你可以写死或查数据库配置
                        List<Integer> userIds = notificationSettingMapper.getUserIdsByTypeKey("alert_level_one");

                        webSocketServer.sendToClientsByInt(userIds, message);
                    }
                }

                break; // 匹配成功后跳出，只插入一次最高等级
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