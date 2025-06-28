package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.model.ViolationProcessingLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ViolationProcessingLogMapper {
    void insert(ViolationProcessingLog log);
    void update(ViolationProcessingLog log);
    List<ViolationProcessingLog> findByViolationId(@Param("violationId") Long violationId);
    ViolationProcessingLog findLatestByViolationId(@Param("violationId") Long violationId);
    int countCompletedAssignees(@Param("violationId") Long violationId, @Param("nodeId") Integer nodeId);
    List<Integer> findAssignedUserIdsForNode(@Param("violationId") Long violationId, @Param("nodeId") Integer nodeId);
}