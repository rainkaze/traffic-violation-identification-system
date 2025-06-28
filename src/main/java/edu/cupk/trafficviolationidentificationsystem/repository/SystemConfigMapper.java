package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SystemConfigMapper {

    // 查询系统配置（假设只有一条，取 id=1）
    @Select("SELECT * FROM system_config WHERE id = 1")
    SystemConfig getConfig();

    // 更新系统配置（通过 id ）
    @Update("""
        UPDATE system_config
        SET system_name = #{systemName},
            session_timeout = #{sessionTimeout},
            data_retention_days = #{dataRetentionDays}
        WHERE id = #{id}
    """)
    int updateConfig(SystemConfig config);


    @Select("SELECT data_retention_days FROM system_config WHERE id = 1")
    Integer getDataRetentionDays();



}
