package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.dto.CountByLabelDto;
import edu.cupk.trafficviolationidentificationsystem.dto.TopLocationDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatisticsMapper {

    /**
     * 按违法类型统计数量
     */
    List<CountByLabelDto> countByViolationType();

    /**
     * 按行政区域统计数量
     */
    List<CountByLabelDto> countByDistrict();

    /**
     * 按小时分布统计数量
     */
    List<CountByLabelDto> countByHour();

    /**
     * 统计过去30天的每日违法数量
     */
    List<CountByLabelDto> countByDayLast30Days();

    /**
     * 查询违法数量最多的前5个地点
     */
    List<TopLocationDto> findTop5Locations();
}