
package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.*;
import edu.cupk.trafficviolationidentificationsystem.model.Device;
import edu.cupk.trafficviolationidentificationsystem.repository.DeviceMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceMapper deviceMapper;
    private final ViolationMapper violationMapper; // 2. 注入 ViolationMapper

    public DeviceServiceImpl(DeviceMapper deviceMapper, ViolationMapper violationMapper) { // 3. 修改构造函数
        this.deviceMapper = deviceMapper;
        this.violationMapper = violationMapper; // 4. 初始化
    }


    private void mapDtoToEntity(DeviceUpsertDto dto, Device entity) {
        entity.setDeviceName(dto.getDeviceName());
        entity.setDeviceType(dto.getDeviceType());
        entity.setDistrictId(dto.getDistrictId());
        entity.setAddress(dto.getAddress());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setRtspUrl(dto.getRtspUrl());

        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus().toUpperCase());
        } else {
            entity.setStatus("OFFLINE");
        }
    }

    @Override
    @Transactional
    public Device createDevice(DeviceUpsertDto deviceDto) {
        Device device = new Device();
        mapDtoToEntity(deviceDto, device);
        // 为 `device_code` 提供一个值
        String uniqueCode = deviceDto.getDeviceName().replaceAll("\\s+", "") + "-" + System.currentTimeMillis() % 1000;
        device.setDeviceCode(uniqueCode);
        deviceMapper.insertDevice(device);
        return device;
    }

    @Override
    @Transactional
    public Device updateDevice(Integer deviceId, DeviceUpsertDto deviceDto) {
        Device device = deviceMapper.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在，ID: " + deviceId));

        mapDtoToEntity(deviceDto, device);
        device.setDeviceId(deviceId);

        deviceMapper.updateDevice(device);
        return device;
    }

    @Override
    @Transactional
    public void deleteDevice(Integer deviceId) {
        deviceMapper.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在，ID: " + deviceId));
        deviceMapper.deleteDeviceById(deviceId);
    }

    @Override
    @Transactional
    public void updateDeviceStatus(Integer deviceId, String status) {
        deviceMapper.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在，ID: " + deviceId));
        deviceMapper.updateDeviceStatus(deviceId, status.toUpperCase());
    }

    @Override
    public List<DeviceListDto> getAllDevices() {
        return deviceMapper.findAllDevices();
    }

    @Override
    public DeviceListDto getDeviceDtoById(Integer deviceId) {
        return deviceMapper.findDtoById(deviceId);
    }

    @Override
    public List<MonitoringCameraDto> getActiveCameras() {
        List<MonitoringCameraDto> cameras = deviceMapper.findActiveCameras();
        cameras.forEach(camera -> {
            // 5. 调用新方法查询并设置真实的违法数量
            long violationCount = violationMapper.countByDeviceId(camera.getDeviceId());
            camera.setViolationCount((int) violationCount);
            // 保持图片URL逻辑不变
            camera.setImageUrl("https://picsum.photos/seed/" + camera.getDeviceId() + "/800/450");
        });
        return cameras;
    }

    @Override
    public List<CountByLabelDto> getDeviceStatusCounts() {
        return deviceMapper.countByStatus();
    }

    @Override
    public List<CountByLabelDto> getDeviceTypeCounts() {
        return deviceMapper.countByType();
    }

    @Override
    public List<DeviceStreamDto> getActiveStreams() {
        return deviceMapper.findActiveStreams();
    }
}
