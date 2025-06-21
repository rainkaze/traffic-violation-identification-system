package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.*;
import edu.cupk.trafficviolationidentificationsystem.model.Device;
import edu.cupk.trafficviolationidentificationsystem.repository.DeviceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.RandomStringUtils; // 导入这个库

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceMapper deviceMapper;

    public DeviceServiceImpl(DeviceMapper deviceMapper) {
        this.deviceMapper = deviceMapper;
    }

    @Override
    public List<DeviceListDto> getAllDevices() {
        return deviceMapper.findAllDevices();
    }

//    @Override
//    @Transactional
//    public Device createDevice(DeviceUpsertDto deviceDto) {
//        deviceMapper.findByDeviceCode(deviceDto.getDeviceCode()).ifPresent(d -> {
//            throw new RuntimeException("设备编码已存在: " + d.getDeviceCode());
//        });
//
//        Device device = new Device();
//        mapDtoToEntity(deviceDto, device);
//
//        deviceMapper.insertDevice(device);
//        return device;
//    }

    @Override
    @Transactional
    public Device createDevice(DeviceUpsertDto deviceDto) {
        // ===== 开始修改：智能处理deviceCode =====

        // 1. 如果手机端没有提供deviceCode，我们为其生成一个
        if (deviceDto.getDeviceCode() == null || deviceDto.getDeviceCode().trim().isEmpty()) {
            // 使用设备名称和一个随机后缀来确保唯一性
            String generatedCode = generateUniqueDeviceCode(deviceDto.getDeviceName());
            deviceDto.setDeviceCode(generatedCode);
        }

        // 2. 检查最终确定的deviceCode是否已存在
        deviceMapper.findByDeviceCode(deviceDto.getDeviceCode()).ifPresent(d -> {
            throw new RuntimeException("设备编码已存在: " + d.getDeviceCode());
        });

        // ===== 结束修改 =====

        Device device = new Device();
        mapDtoToEntity(deviceDto, device);
        device.setStatus("UNBOUND");
        device.setBindingCode(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
        device.setBindingCodeExpiresAt(LocalDateTime.now().plusHours(24)); // 绑定码24小时有效


        deviceMapper.insertDevice(device);
        return device;
    }
    // 新增一个私有辅助方法来生成编码
    private String generateUniqueDeviceCode(String deviceName) {
        // 将名称中的空格替换为'-'，并取前10个字符，确保不会太长
        String baseName = deviceName.replaceAll("\\s+", "-");
        if (baseName.length() > 10) {
            baseName = baseName.substring(0, 10);
        }
        // 拼接一个6位的随机字母和数字，并转为大写
        return baseName + "-" + RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    @Override
    @Transactional
    public Device updateDevice(Integer deviceId, DeviceUpsertDto deviceDto) {
        Device device = deviceMapper.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在，ID: " + deviceId));

        // [核心修改] 调用一个安全的映射方法
        mapDtoToEntityForUpdate(deviceDto, device);
        device.setDeviceId(deviceId); // 确保ID不变

        deviceMapper.updateDevice(device);
        return device;
    }
    // [新增] 一个专门用于更新的安全映射方法
    private void mapDtoToEntityForUpdate(DeviceUpsertDto dto, Device entity) {
        entity.setDeviceName(dto.getDeviceName());
        entity.setDeviceType(dto.getDeviceType());
        entity.setDistrictId(dto.getDistrictId());
        entity.setAddress(dto.getAddress());
        entity.setModelName(dto.getModelName());
        entity.setIpAddress(dto.getIpAddress());
        // 注意：这里我们故意不映射 status 字段，因为状态由系统自动管理
    }
    @Override
    @Transactional
    public void deleteDevice(Integer deviceId) {
        deviceMapper.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在，ID: " + deviceId));
        deviceMapper.deleteDeviceById(deviceId);
    }

    @Override
    public List<MonitoringCameraDto> getActiveCameras() {
        List<MonitoringCameraDto> cameras = deviceMapper.findActiveCameras();
        // 模拟违法数量和图片URL
        Random random = new Random();
        cameras.forEach(camera -> {
            camera.setViolationCount(random.nextInt(5)); // 0-4
            camera.setImageUrl("https://picsum.photos/seed/" + camera.getDeviceId() + "/800/450");
        });
        return cameras;
    }

    private void mapDtoToEntity(DeviceUpsertDto dto, Device entity) {
        entity.setDeviceCode(dto.getDeviceCode());
        entity.setDeviceName(dto.getDeviceName());
        entity.setDeviceType(dto.getDeviceType());
        entity.setDistrictId(dto.getDistrictId());
        entity.setAddress(dto.getAddress());
        entity.setModelName(dto.getModelName());
        entity.setIpAddress(dto.getIpAddress());
        entity.setStatus(dto.getStatus());
    }
    @Override
    public DeviceListDto getDeviceDtoById(Integer deviceId) {
        return deviceMapper.findDtoById(deviceId);
    }

    @Override
    public List<CountByLabelDto> getDeviceStatusCounts() {
        return deviceMapper.countByStatus();
    }

    @Override
    public List<CountByLabelDto> getDeviceTypeCounts() {
        return deviceMapper.countByType();
    }

    // [新增] 实现设备绑定逻辑
    @Override
    @Transactional
    public Device bindDevice(DeviceBindingDto bindingDto) {
        Device device = deviceMapper.findByBindingCode(bindingDto.getBindingCode())
                .orElseThrow(() -> new RuntimeException("无效的绑定码。"));

        if (device.getBindingCodeExpiresAt() != null && device.getBindingCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("绑定码已过期，请联系管理员重新生成。");
        }

        // 绑定成功
        device.setStatus("OFFLINE"); // 绑定后状态为离线，等待心跳
        device.setBoundAt(LocalDateTime.now());
        device.setBindingCode(null); // 绑定码一次性使用，用后作废
        device.setBindingCodeExpiresAt(null);
        deviceMapper.updateDevice(device);

        return device;
    }

    // [新增] 实现状态更新
    @Override
    @Transactional
    public void updateDeviceStatus(Integer deviceId, String status) {
        deviceMapper.updateDeviceStatus(deviceId, status);
    }

    // [新增] 实现重新生成绑定码
    @Override
    @Transactional
    public String generateNewBindingCode(Integer deviceId) {
        Device device = deviceMapper.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("设备不存在，ID: " + deviceId));

        String newBindingCode = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        device.setBindingCode(newBindingCode);
        device.setBindingCodeExpiresAt(LocalDateTime.now().plusHours(24));
        deviceMapper.updateDevice(device);

        return newBindingCode;
    }
}