package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.dto.CountByLabelDto;
import edu.cupk.trafficviolationidentificationsystem.dto.DeviceListDto;
import edu.cupk.trafficviolationidentificationsystem.dto.MonitoringCameraDto;
import edu.cupk.trafficviolationidentificationsystem.model.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DeviceMapper {
    List<DeviceListDto> findAllDevices();
    Optional<Device> findById(@Param("deviceId") Integer deviceId);
    Optional<Device> findByDeviceCode(@Param("deviceCode") String deviceCode);
    int insertDevice(Device device);
    int updateDevice(Device device);
    int deleteDeviceById(@Param("deviceId") Integer deviceId);
    List<MonitoringCameraDto> findActiveCameras();

    // 新增：根据ID查询单个设备的DTO
    DeviceListDto findDtoById(@Param("deviceId") Integer deviceId);

    // 新增：按状态统计设备数量
    List<CountByLabelDto> countByStatus();

    // 新增：按类型统计设备数量
    List<CountByLabelDto> countByType();

    // [新增] 根据绑定码查找设备
    Optional<Device> findByBindingCode(@Param("bindingCode") String bindingCode);

    // [新增] 更新设备状态
    int updateDeviceStatus(@Param("deviceId") Integer deviceId, @Param("status") String status);

}