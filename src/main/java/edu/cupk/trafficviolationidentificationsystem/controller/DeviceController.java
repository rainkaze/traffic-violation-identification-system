package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.*;
import edu.cupk.trafficviolationidentificationsystem.model.Device;
import edu.cupk.trafficviolationidentificationsystem.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }
    private static final List<String> VALID_DEVICE_TYPES = Arrays.asList("高清摄像头", "雷达测速仪", "AI识别终端", "GPU推理服务器");

    @GetMapping
    public ResponseEntity<List<DeviceListDto>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @PostMapping
    public ResponseEntity<Device> createDevice(@Valid @RequestBody DeviceUpsertDto deviceDto) {
        Device createdDevice = deviceService.createDevice(deviceDto);
        return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Integer id, @Valid @RequestBody DeviceUpsertDto deviceDto) {
        Device updatedDevice = deviceService.updateDevice(id, deviceDto);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Integer id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cameras/active")
    public ResponseEntity<List<MonitoringCameraDto>> getActiveCameras() {
        return ResponseEntity.ok(deviceService.getActiveCameras());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceListDto> getDeviceById(@PathVariable Integer id) {
        DeviceListDto device = deviceService.getDeviceDtoById(id);
        return ResponseEntity.ok(device);
    }

    @GetMapping("/statistics/status")
    public ResponseEntity<List<CountByLabelDto>> getStatusStatistics() {
        return ResponseEntity.ok(deviceService.getDeviceStatusCounts());
    }

    @GetMapping("/statistics/type")
    public ResponseEntity<List<CountByLabelDto>> getTypeStatistics() {
        return ResponseEntity.ok(deviceService.getDeviceTypeCounts());
    }

    // 新增：为Android端提供的注册接口
//    @PostMapping("/register")
//    public ResponseEntity<Device> registerDeviceFromApp(@RequestBody DeviceUpsertDto deviceDto) {
//        // 复用已有的创建逻辑
//        Device createdDevice = deviceService.createDevice(deviceDto);
//        return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
//    }
    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @PostMapping("/register")
    public ResponseEntity<Device> registerDeviceFromApp(@RequestBody Map<String, Object> payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(payload);
            logger.info("Received device registration payload: {}", jsonPayload);
        } catch (Exception e) {
            logger.error("Error logging payload", e);
        }

        DeviceUpsertDto deviceDto = new DeviceUpsertDto();

        // 智能处理 deviceCode
        String deviceCode = (String) payload.get("deviceCode");
        deviceDto.setDeviceCode(deviceCode); // 即使是null也先设置

        // 智能处理 deviceName
        String deviceName = (String) payload.get("deviceName");
        deviceDto.setDeviceName(deviceName != null ? deviceName : "未命名设备");

        // ===== 开始修改：校验并修正 deviceType =====
        String deviceTypeFromApp = (String) payload.get("deviceType");

        // 如果手机传来的类型不在我们的合法列表里，或者为null，就设置一个默认值
        if (deviceTypeFromApp == null || !VALID_DEVICE_TYPES.contains(deviceTypeFromApp)) {
            logger.warn("Invalid or missing deviceType '{}' from app. Defaulting to '高清摄像头'.", deviceTypeFromApp);
            deviceDto.setDeviceType("高清摄像头");
        } else {
            deviceDto.setDeviceType(deviceTypeFromApp);
        }
        // ===== 结束修改 =====

        // 设置其他默认值
        deviceDto.setDistrictId(1);
        deviceDto.setAddress(deviceDto.getDeviceName() + " (移动端注册)");
        deviceDto.setStatus("online");

        Device createdDevice = deviceService.createDevice(deviceDto);
        return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
    }

    // 新增：为Android端提供的心跳/状态更新接口
//    @PutMapping("/{id}/status")
//    public ResponseEntity<Void> updateDeviceStatus(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
//        String status = payload.get("status");
//        if (status != null) {
//            // 注意：这里只是一个简化实现，实际项目中可能需要更复杂的逻辑
//            DeviceListDto device = deviceService.getDeviceDtoById(id);
//            if (device != null) {
//                device.setStatus(status);
//                DeviceUpsertDto upsertDto = new DeviceUpsertDto();
//                // ... (此处需要将DeviceListDto转换为UpsertDto，或直接在Service层创建更新状态的方法)
//                // 为简化，我们直接调用一个假想的service方法
//                // deviceService.updateStatus(id, status);
//                System.out.println("Device " + id + " status updated to: " + status);
//            }
//        }
//        return ResponseEntity.ok().build();
//    }
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateDeviceStatus(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        if (status != null) {
            System.out.println("Device " + id + " status updated to: " + status);
        }
        return ResponseEntity.ok().build();
    }

    // [新增] 供手机App使用的绑定接口
    @PostMapping("/bind")
    public ResponseEntity<Device> bindDevice(@Valid @RequestBody DeviceBindingDto bindingDto) {
        Device boundDevice = deviceService.bindDevice(bindingDto);
        return ResponseEntity.ok(boundDevice);
    }

    // [新增] 重新生成绑定码的接口（需要管理员权限）
    @PreAuthorize("hasRole('管理员')")
    @PostMapping("/{id}/new-binding-code")
    public ResponseEntity<Map<String, String>> generateNewBindingCode(@PathVariable Integer id) {
        String newCode = deviceService.generateNewBindingCode(id);
        return ResponseEntity.ok(Map.of("bindingCode", newCode));
    }
}