package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.entity.WarningRule;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WarningRuleMapper {
    @Select("SELECT * FROM warning_rules ORDER BY level, id")
    List<WarningRule> findAll();

    @Insert("INSERT INTO warning_rules(level, violation_type, min_confidence, description) " +
            "VALUES(#{level}, #{violationType}, #{minConfidence}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WarningRule rule);

    @Update("UPDATE warning_rules SET level = #{level}, violation_type = #{violationType}, " +
            "min_confidence = #{minConfidence}, description = #{description} WHERE id = #{id}")
    int update(WarningRule rule);

    @Delete("DELETE FROM warning_rules WHERE id = #{id}")
    int deleteById(int id);

    @Delete("DELETE FROM warning_rules WHERE level = #{level}")
    int deleteByLevel(String level);
}
