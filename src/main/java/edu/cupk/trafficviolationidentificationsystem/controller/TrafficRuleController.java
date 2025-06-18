package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.TrafficRuleDto;
import edu.cupk.trafficviolationidentificationsystem.service.TrafficRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rules") // 我们为法规相关的API创建一个新的路径
public class TrafficRuleController {

    private final TrafficRuleService trafficRuleService;

    public TrafficRuleController(TrafficRuleService trafficRuleService) {
        this.trafficRuleService = trafficRuleService;
    }

    /**
     * 获取所有可用的违法类型列表
     * @return 字符串列表, 如 ["超速行驶", "闯红灯", ...]
     */
    @GetMapping("/types")
    public ResponseEntity<List<String>> getViolationTypes() {
        List<String> types = trafficRuleService.getAllViolationTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping
    public ResponseEntity<List<TrafficRuleDto>> getAllRules() {
        return ResponseEntity.ok(trafficRuleService.getAllRules());
    }

    // 新增接口：分页查询
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getRulesPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        int offset = (page - 1) * size;
        List<TrafficRuleDto> items = trafficRuleService.getRulesByPage(offset, size);
        int total = trafficRuleService.countAllRules();

        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("items", items);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TrafficRuleDto> createRule(@RequestBody TrafficRuleDto trafficRuleDto) {
    TrafficRuleDto createdRule = trafficRuleService.createRule(trafficRuleDto);
    return ResponseEntity.ok(createdRule);
    }

    @PutMapping("/{ruleId}")
    public ResponseEntity<TrafficRuleDto> updateRule(@PathVariable Long ruleId, @RequestBody TrafficRuleDto trafficRuleDto) {
    trafficRuleDto.setRuleId(Math.toIntExact(ruleId)); // 确保ID正确设置
    TrafficRuleDto updatedRule = trafficRuleService.updateRule(trafficRuleDto);
    return ResponseEntity.ok(updatedRule);
    }




}