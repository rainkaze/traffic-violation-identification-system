package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.TrafficRuleDto;
import edu.cupk.trafficviolationidentificationsystem.service.TrafficRuleService;
import edu.cupk.trafficviolationidentificationsystem.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rules") // 我们为法规相关的API创建一个新的路径
public class TrafficRuleController {

    @Autowired
    private WebSocketServer webSocketServer;

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
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String searchRuleKeyword
    ) {
//        System.out.println("page"+ page);
//        System.out.println("size"+ size);
//        System.out.println("searchRuleKeyword"+ searchRuleKeyword);

        int offset = (page - 1) * size;
        List<TrafficRuleDto> items = trafficRuleService.getRulesByPage(offset, size, searchRuleKeyword);
        int total = trafficRuleService.countAllRules();

        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("items", items);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TrafficRuleDto> createRule(@RequestBody TrafficRuleDto trafficRuleDto) {


//        List<Integer> a=new ArrayList<>();
//        a.add(8);
//        webSocketServer.sendToClientsByInt(a,"新增条款");

    TrafficRuleDto createdRule = trafficRuleService.createRule(trafficRuleDto);
    return ResponseEntity.ok(createdRule);
    }

    @PutMapping("/{ruleId}")
    public ResponseEntity<TrafficRuleDto> updateRule(@PathVariable Long ruleId, @RequestBody TrafficRuleDto trafficRuleDto) {
    trafficRuleDto.setRuleId(Math.toIntExact(ruleId)); // 确保ID正确设置
    TrafficRuleDto updatedRule = trafficRuleService.updateRule(trafficRuleDto);
    return ResponseEntity.ok(updatedRule);
    }


    @DeleteMapping("/{ruleId}")
    public ResponseEntity updateRule(@PathVariable Long ruleId) {
            //删除
        trafficRuleService.deleteRule(ruleId);
        return ResponseEntity.ok().build();
    }


}