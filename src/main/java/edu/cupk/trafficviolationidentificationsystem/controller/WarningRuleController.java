package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.entity.WarningRule;
import edu.cupk.trafficviolationidentificationsystem.repository.WarningRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/warningRules")
public class WarningRuleController {
//    @GetMapping("")
//    public List<WarningRule> getWarningRules() {
//        System.out.println("获取预警规则");
//        return Arrays.asList(
//                new WarningRule(1, "一级预警", "闯红灯", 0.8, "闯红灯且置信度不低于0.8，触发一级预警"),
//                new WarningRule(2, "一级预警", "逆行", 0.8, "逆行且置信度不低于0.8，触发一级预警"),
//                new WarningRule(3, "二级预警", "超速", 0.6, "超速且置信度不低于0.6，触发二级预警")
//        );
//    }

    @Autowired
    private WarningRuleMapper warningRuleMapper;

    @GetMapping
    public List<WarningRule> getAllRules() {
        return warningRuleMapper.findAll();
    }

    @PostMapping
    public WarningRule addRule(@RequestBody WarningRule rule) {
        // 自动生成描述
        rule.setDescription(rule.getViolationType() + "且置信度不低于" + rule.getMinConfidence() + "，触发" + rule.getLevel());
        warningRuleMapper.insert(rule);
        return rule;
    }

    @PutMapping("/{id}")
    public WarningRule updateRule(@PathVariable int id, @RequestBody WarningRule rule) {
        rule.setId(id);
        rule.setDescription(rule.getViolationType() + "且置信度不低于" + rule.getMinConfidence() + "，触发" + rule.getLevel());
        warningRuleMapper.update(rule);
        return rule;
    }

    @DeleteMapping("/{id}")
    public void deleteRule(@PathVariable int id) {
        warningRuleMapper.deleteById(id);
    }

    // 额外接口：批量删除某个等级所有规则
    @DeleteMapping("/level/{level}")
    public void deleteRulesByLevel(@PathVariable String level) {
        warningRuleMapper.deleteByLevel(level);
    }

}
