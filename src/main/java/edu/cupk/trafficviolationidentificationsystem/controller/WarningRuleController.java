package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.entity.WarningRule;
import edu.cupk.trafficviolationidentificationsystem.repository.WarningRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预警规则管理控制器。
 * 提供对违法行为预警规则的增、删、改、查功能。
 */
@RestController
@RequestMapping("/api/warningRules")
public class WarningRuleController {

    @Autowired
    private WarningRuleMapper warningRuleMapper;

    /**
     * 获取所有预警规则。
     *
     * @return 预警规则列表。
     */
    @GetMapping
    public List<WarningRule> getAllRules() {
        return warningRuleMapper.findAll();
    }

    /**
     * 添加一条新的预警规则。
     *
     * @param rule 包含新规则信息的实体。
     * @return 创建成功后返回包含ID的新规则实体。
     */
    @PostMapping
    @AuditLog(actionType = "CREATE_WARNING_RULE", targetEntityType = "WARNING_RULE", targetEntityIdExpression = "#result.id")
    public WarningRule addRule(@RequestBody WarningRule rule) {
        rule.setDescription(String.format("%s且置信度不低于%.2f，触发%s",
                rule.getViolationType(), rule.getMinConfidence(), rule.getLevel()));
        warningRuleMapper.insert(rule);
        return rule;
    }

    /**
     * 更新一条已存在的预警规则。
     *
     * @param id   要更新的规则ID。
     * @param rule 包含更新后规则信息的实体。
     * @return 更新后的规则实体。
     */
    @PutMapping("/{id}")
    @AuditLog(actionType = "UPDATE_WARNING_RULE", targetEntityType = "WARNING_RULE", targetEntityIdExpression = "#id")
    public WarningRule updateRule(@PathVariable int id, @RequestBody WarningRule rule) {
        rule.setId(id);
        rule.setDescription(String.format("%s且置信度不低于%.2f，触发%s",
                rule.getViolationType(), rule.getMinConfidence(), rule.getLevel()));
        warningRuleMapper.update(rule);
        return rule;
    }

    /**
     * 删除一条预警规则。
     *
     * @param id 要删除的规则ID。
     */
    @DeleteMapping("/{id}")
    @AuditLog(actionType = "DELETE_WARNING_RULE", targetEntityType = "WARNING_RULE", targetEntityIdExpression = "#id")
    public void deleteRule(@PathVariable int id) {
        warningRuleMapper.deleteById(id);
    }

    /**
     * 按等级批量删除预警规则。
     *
     * @param level 要删除的预警等级，例如 "一级预警"。
     */
    @DeleteMapping("/level/{level}")
    @AuditLog(actionType = "DELETE_WARNING_RULES_BY_LEVEL", targetEntityType = "WARNING_RULE_LEVEL", targetEntityIdExpression = "#level")
    public void deleteRulesByLevel(@PathVariable String level) {
        warningRuleMapper.deleteByLevel(level);
    }
}