package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.TrafficRuleDto;
import edu.cupk.trafficviolationidentificationsystem.repository.TrafficRuleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TrafficRuleService {
    List<String> getAllViolationTypes();
    List<TrafficRuleDto> getAllRules();

    int countAllRules();
    // 新增：分页查询
    List<TrafficRuleDto> getRulesByPage(int offset, int size, String searchRuleKeyword);


    TrafficRuleDto createRule(TrafficRuleDto trafficRuleDto);

    TrafficRuleDto updateRule(TrafficRuleDto trafficRuleDto);

    //删除
    void deleteRule(Long ruleId);
}

@Service
class TrafficRuleServiceImpl implements TrafficRuleService {

    private final TrafficRuleMapper trafficRuleMapper;

    public TrafficRuleServiceImpl(TrafficRuleMapper trafficRuleMapper) {
        this.trafficRuleMapper = trafficRuleMapper;
    }
    @Override
    public List<String> getAllViolationTypes() {
        // 直接调用mapper方法获取数据
        return trafficRuleMapper.findAllViolationTypes();
    }

    @Override
    public List<TrafficRuleDto> getAllRules() {
        return trafficRuleMapper.findAllRules();
    }


    @Override
    public List<TrafficRuleDto> getRulesByPage(int offset, int size, String searchRuleKeyword) {
        return trafficRuleMapper.findRulesByOffsetAndSize(offset, size, searchRuleKeyword);
    }

    @Override
    public int countAllRules() {
        return trafficRuleMapper.countAllRules();
    }


    // 创建规则
    public TrafficRuleDto createRule(TrafficRuleDto rule) {
        trafficRuleMapper.insertRule(rule);
        return rule; // 或者根据需求重新查询一次
    }

    // 更新规则
    public TrafficRuleDto updateRule(TrafficRuleDto rule) {
        trafficRuleMapper.updateRule(rule);
        return rule;
    }



    public void deleteRule(Long ruleId) {
        trafficRuleMapper.deleteRule(ruleId);
    }




}