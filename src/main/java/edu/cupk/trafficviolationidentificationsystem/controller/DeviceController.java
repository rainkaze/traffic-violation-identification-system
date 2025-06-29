package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.*;
import edu.cupk.trafficviolationidentificationsystem.model.Device;
import edu.cupk.trafficviolationidentificationsystem.service.DeviceService;
import edu.cupk.trafficviolationidentificationsystem.service.ViolationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final ViolationService violationService;

    // 合并后的构造函数，注入了两个Service
    public DeviceController(DeviceService deviceService, ViolationService violationService) {
        this.deviceService = deviceService;
        this.violationService = violationService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceListDto>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @PostMapping
    public ResponseEntity<Device> createDevice(@Valid @RequestBody DeviceUpsertDto deviceDto) {
        // 保留的修复：强制将状态转换为大写
        if (deviceDto.getStatus() != null) {
            deviceDto.setStatus(deviceDto.getStatus().toUpperCase());
        }
        Device createdDevice = deviceService.createDevice(deviceDto);
        return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Integer id, @Valid @RequestBody DeviceUpsertDto deviceDto) {
        // 保留的修复：同样在更新时强制转换为大写
        if (deviceDto.getStatus() != null) {
            deviceDto.setStatus(deviceDto.getStatus().toUpperCase());
        }
        Device updatedDevice = deviceService.updateDevice(id, deviceDto);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Integer id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceListDto> getDeviceById(@PathVariable Integer id) {
        return ResponseEntity.ok(deviceService.getDeviceDtoById(id));
    }

    @GetMapping("/cameras/active")
    public ResponseEntity<List<MonitoringCameraDto>> getActiveCameras() {
        return ResponseEntity.ok(deviceService.getActiveCameras());
    }

    /**
     * [来自版本1的新增功能] 为AI模型提供动态的、可用的RTSP流列表
     * @return 一个包含设备ID和RTSP地址的列表
     */
    @GetMapping("/streams")
    public ResponseEntity<List<DeviceStreamDto>> getActiveStreamsForModel() {
        return ResponseEntity.ok(deviceService.getActiveStreams());
    }

    /**
     * [来自版本2的新增功能]：设备状态统计接口
     */
    @GetMapping("/statistics/status")
    public ResponseEntity<List<CountByLabelDto>> getDeviceStatusCounts() {
        return ResponseEntity.ok(deviceService.getDeviceStatusCounts());
    }

    /**
     * [来自版本2的新增功能]：设备类型统计接口
     */
    @GetMapping("/statistics/type")
    public ResponseEntity<List<CountByLabelDto>> getDeviceTypeCounts() {
        return ResponseEntity.ok(deviceService.getDeviceTypeCounts());
    }

    /**
     * [来自版本2的新增功能]：获取单个设备关联的违法记录
     */
    @GetMapping("/{id}/violations")
    public ResponseEntity<List<ViolationDetailDto>> getRecentViolationsForDevice(@PathVariable("id") Integer deviceId) {
        List<ViolationDetailDto> violations = violationService.getRecentViolationsByDeviceId(deviceId);
        return ResponseEntity.ok(violations);
    }
}