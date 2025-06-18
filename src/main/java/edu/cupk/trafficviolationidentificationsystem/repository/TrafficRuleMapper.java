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
}