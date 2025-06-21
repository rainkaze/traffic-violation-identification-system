package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.CountByLabelDto;
import edu.cupk.trafficviolationidentificationsystem.dto.DeviceListDto;
import edu.cupk.trafficviolationidentificationsystem.dto.DeviceUpsertDto;
import edu.cupk.trafficviolationidentificationsystem.dto.MonitoringCameraDto;
import edu.cupk.trafficviolationidentificationsystem.model.Device;

import java.util.List;

public interface DeviceService {
    List<DeviceListDto> getAllDevices();
    Device createDevice(DeviceUpsertDto deviceDto);
    Device updateDevice(Integer deviceId, DeviceUpsertDto deviceDto);
    void deleteDevice(Integer deviceId);
    List<MonitoringCameraDto> getActiveCameras();
    DeviceListDto getDeviceDtoById(Integer deviceId);
    List<CountByLabelDto> getDeviceStatusCounts();
    List<CountByLabelDto> getDeviceTypeCounts();
}