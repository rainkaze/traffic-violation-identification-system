package edu.cupk.trafficviolationidentificationsystem.repository;
import edu.cupk.trafficviolationidentificationsystem.dto.AudilVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AuditLogMapper {


    List<AudilVo> selectPage1();


}
