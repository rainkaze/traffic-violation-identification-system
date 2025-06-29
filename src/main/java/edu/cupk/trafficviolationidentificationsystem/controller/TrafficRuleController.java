package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.dto.TrafficRuleDto;
import edu.cupk.trafficviolationidentificationsystem.service.TrafficRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交通法规管理控制器。
 * 提供对交通法规的增、删、改、查及类型查询等API接口。
 */
@RestController
@RequestMapping("/api/rules")
public class TrafficRuleController {

    private final TrafficRuleService trafficRuleService;

    /**
     * 构造函数，用于注入交通法规服务。
     *
     * @param trafficRuleService 交通法规服务实例。
     */
    public TrafficRuleController(TrafficRuleService trafficRuleService) {
        this.trafficRuleService = trafficRuleService;
    }

    /**
     * 获取所有可用的、不重复的违法行为类型列表。
     *
     * @return 一个包含违法类型的字符串列表, 例如 ["超速行驶", "闯红灯", ...]。
     */
    @GetMapping("/types")
    public ResponseEntity<List<String>> getViolationTypes() {
        List<String> types = trafficRuleService.getAllViolationTypes();
        return ResponseEntity.ok(types);
    }

    /**
     * 获取所有交通法规的完整列表（不分页）。
     *
     * @return 包含所有交通法规DTO的列表。
     */
    @GetMapping
    public ResponseEntity<List<TrafficRuleDto>> getAllRules() {
        return ResponseEntity.ok(trafficRuleService.getAllRules());
    }

    /**
     * 分页并按关键字搜索交通法规。
     *
     * @param page              当前页码，默认为 1。
     * @param size              每页条目数，默认为 5。
     * @param searchRuleKeyword 用于搜索法规名称或内容的关键字（可选）。
     * @return 包含分页数据和总条目数的Map对象。
     */
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getRulesPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String searchRuleKeyword
    ) {
        int offset = (page - 1) * size;
        List<TrafficRuleDto> items = trafficRuleService.getRulesByPage(offset, size, searchRuleKeyword);
        int total = trafficRuleService.countAllRules();

        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("items", items);

        return ResponseEntity.ok(response);
    }

    /**
     * 创建一条新的交通法规。
     *
     * @param trafficRuleDto 包含新法规信息的DTO。
     * @return 创建成功后返回新创建的法规实体及HTTP 201 CREATED状态。
     */
    @PostMapping
    @AuditLog(actionType = "CREATE_TRAFFIC_RULE", targetEntityType = "TRAFFIC_RULE", targetEntityIdExpression = "#result.body.ruleId")
    public ResponseEntity<TrafficRuleDto> createRule(@RequestBody TrafficRuleDto trafficRuleDto) {
        TrafficRuleDto createdRule = trafficRuleService.createRule(trafficRuleDto);
        return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    }

    /**
     * 更新一条已存在的交通法规。
     *
     * @param ruleId         要更新的法规ID。
     * @param trafficRuleDto 包含更新后法规信息的DTO。
     * @return 更新成功后返回更新后的法规实体。
     */
    @PutMapping("/{ruleId}")
    @AuditLog(actionType = "UPDATE_TRAFFIC_RULE", targetEntityType = "TRAFFIC_RULE", targetEntityIdExpression = "#ruleId")
    public ResponseEntity<TrafficRuleDto> updateRule(@PathVariable Long ruleId, @RequestBody TrafficRuleDto trafficRuleDto) {
        trafficRuleDto.setRuleId(Math.toIntExact(ruleId)); // 确保ID被正确设置以进行更新
        TrafficRuleDto updatedRule = trafficRuleService.updateRule(trafficRuleDto);
        return ResponseEntity.ok(updatedRule);
    }

    /**
     * 删除一条交通法规。
     *
     * @param ruleId 要删除的法规ID。
     * @return 成功时返回HTTP 204 No Content。
     */
    @DeleteMapping("/{ruleId}")
    @AuditLog(actionType = "DELETE_TRAFFIC_RULE", targetEntityType = "TRAFFIC_RULE", targetEntityIdExpression = "#ruleId")
    public ResponseEntity<Void> deleteRule(@PathVariable Long ruleId) {
        trafficRuleService.deleteRule(ruleId);
        return ResponseEntity.noContent().build();
    }
}