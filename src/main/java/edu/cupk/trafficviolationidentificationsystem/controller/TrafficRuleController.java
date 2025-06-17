package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.service.TrafficRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}