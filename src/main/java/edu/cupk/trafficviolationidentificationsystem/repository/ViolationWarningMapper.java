package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.entity.ViolationWarning;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ViolationWarningMapper {

    @Insert("INSERT INTO violation_warnings(violation_id, warning_level) VALUES(#{violationId}, #{warningLevel})")
    int insert(ViolationWarning warning);

    @Select("SELECT * FROM violation_warnings WHERE violation_id = #{violationId}")
    ViolationWarning findByViolationId(Long violationId);
}