package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.*;
import edu.cupk.trafficviolationidentificationsystem.model.Device;

import java.util.List;

public interface DeviceService {
    List<DeviceListDto> getAllDevices();
    Device createDevice(DeviceUpsertDto deviceDto);
    Device updateDevice(Integer deviceId, DeviceUpsertDto deviceDto);
    void deleteDevice(Integer deviceId);
    DeviceListDto getDeviceDtoById(Integer deviceId);
    List<MonitoringCameraDto> getActiveCameras();
    List<CountByLabelDto> getDeviceStatusCounts();
    List<CountByLabelDto> getDeviceTypeCounts();
    void updateDeviceStatus(Integer deviceId, String status);
    List<DeviceStreamDto> getActiveStreams();
}

