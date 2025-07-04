package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.dto.TrafficRuleDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TrafficRuleMapper {

    /**
     * 查询所有不重复的违法类型名称
     * @return 违法类型字符串列表
     */
    List<String> findAllViolationTypes();
    List<TrafficRuleDto> findAllRules();

    List<TrafficRuleDto> findRulesByOffsetAndSize(int offset, int size,String searchRuleKeyword);

    // 新增：获取总数
    int countAllRules();

    void insertRule(TrafficRuleDto rule);

    void updateRule(TrafficRuleDto rule);

    void deleteRule(Long ruleId);
}