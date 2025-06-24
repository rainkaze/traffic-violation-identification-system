package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.dto.AccidentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface AccidentMapper {

    List<AccidentDto> getDevicesWithLocation(@Param("status") String status, @Param("districtName") String districtName);

}
