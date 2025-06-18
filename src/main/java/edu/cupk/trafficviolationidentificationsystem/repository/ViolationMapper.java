package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ViolationMapper {

    /**
     * 根据查询条件动态查询违法记录列表
     * @param query 查询 DTO
     * @param pageSize 每页大小
     * @param offset 偏移量
     * @return 违法记录详情列表
     */
    List<ViolationDetailDto> findViolationsByCriteria(@Param("query") ViolationQueryDto query, @Param("pageSize") int pageSize, @Param("offset") int offset);

    /**
     * 根据相同的查询条件计算总记录数
     * @param query 查询 DTO
     * @return 总记录数
     */
    long countViolationsByCriteria(@Param("query") ViolationQueryDto query);
}