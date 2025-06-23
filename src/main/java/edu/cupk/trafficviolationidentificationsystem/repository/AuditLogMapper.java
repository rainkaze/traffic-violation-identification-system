package edu.cupk.trafficviolationidentificationsystem.repository;
import edu.cupk.trafficviolationidentificationsystem.dto.AudilVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AuditLogMapper {


    List<AudilVo> selectPage1();

    @Delete("DELETE FROM traffic_violation_system_02.audit_logs WHERE audit_logs.created_at < #{threshold}")
    int deleteBefore(@Param("threshold") LocalDateTime threshold);




}
