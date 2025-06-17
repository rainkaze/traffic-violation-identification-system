package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.repository.TrafficRuleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TrafficRuleService {
    List<String> getAllViolationTypes();
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
}