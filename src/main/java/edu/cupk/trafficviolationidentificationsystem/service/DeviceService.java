package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.*;
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

    // [新增] 手机端绑定设备的方法
    Device bindDevice(DeviceBindingDto bindingDto);

    // [新增] 更新设备状态的方法
    void updateDeviceStatus(Integer deviceId, String status);

    // [新增] 为设备生成新的绑定码
    String generateNewBindingCode(Integer deviceId);
}