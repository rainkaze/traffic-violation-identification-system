package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationQueryDto;
import edu.cupk.trafficviolationidentificationsystem.model.Violation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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

    /**
     * 插入一条测试用的违法记录
     * @param violation 违法记录实体
     * @return 影响的行数
     */
    int insertTestViolation(Violation violation);

    /**
     * 获取最新的N条测试违法记录
     * @param limit 记录数量
     * @return 违法记录详情列表
     */
    List<ViolationDetailDto> getLatestTestViolations(@Param("limit") int limit);

    /**
     * 根据ID查找单个违法记录实体
     * @param violationId 违法记录ID
     * @return Violation 实体
     */
    Violation findById(@Param("violationId") Long violationId);

    /**
     * 更新违法记录的状态
     * @param violationId 违法记录ID
     * @param status 新的状态
     */
    void updateStatus(@Param("violationId") Long violationId, @Param("status") String status);

    /**
     * 根据ID查找单个违法记录的详细信息 DTO
     * @param id 违法记录ID
     * @return ViolationDetailDto
     */
    ViolationDetailDto findViolationDetailById(@Param("id") Long id);

    /**
     * 删除指定时间点之前的所有记录
     * @param threshold 时间阈值
     * @return 删除的行数
     */
    @Delete("DELETE FROM violations WHERE created_at < #{threshold}")
    int deleteBefore(@Param("threshold") LocalDateTime threshold);

    /**
     * (新增) 根据查询条件查询所有匹配的违法记录 (不分页，用于导出)
     * @param query 查询 DTO
     * @return 完整的违法记录详情列表
     */
    List<ViolationDetailDto> findAllViolationsByCriteria(@Param("query") ViolationQueryDto query);

    /**
     * [新增] 根据设备ID查询最近的违法记录
     * @param deviceId 设备ID
     * @return 违法记录详情列表
     */
    List<ViolationDetailDto> findRecentViolationsByDeviceId(@Param("deviceId") Integer deviceId);

    /**
     * [新增] 根据设备ID统计违法记录总数
     * @param deviceId 设备ID
     * @return 总记录数
     */
    long countByDeviceId(@Param("deviceId") Integer deviceId);
}