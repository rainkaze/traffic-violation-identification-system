package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.CountByLabelDto;
import edu.cupk.trafficviolationidentificationsystem.dto.DeviceListDto;
import edu.cupk.trafficviolationidentificationsystem.dto.DeviceUpsertDto;
import edu.cupk.trafficviolationidentificationsystem.dto.MonitoringCameraDto;
import edu.cupk.trafficviolationidentificationsystem.model.Device;
import edu.cupk.trafficviolationidentificationsystem.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceListDto>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @PostMapping
    public ResponseEntity<Device> createDevice(@Valid @RequestBody DeviceUpsertDto deviceDto) {
        // *** 关键修复：强制将状态转换为大写，确保与数据库ENUM匹配 ***
        if (deviceDto.getStatus() != null) {
            deviceDto.setStatus(deviceDto.getStatus().toUpperCase());
        }
        Device createdDevice = deviceService.createDevice(deviceDto);
        return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Integer id, @Valid @RequestBody DeviceUpsertDto deviceDto) {
        // *** 关键修复：同样在更新时强制转换为大写 ***
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

    // [新增]：设备状态统计接口
    @GetMapping("/statistics/status")
    public ResponseEntity<List<CountByLabelDto>> getDeviceStatusCounts() {
        return ResponseEntity.ok(deviceService.getDeviceStatusCounts());
    }

    // [新增]：设备类型统计接口
    @GetMapping("/statistics/type")
    public ResponseEntity<List<CountByLabelDto>> getDeviceTypeCounts() {
        return ResponseEntity.ok(deviceService.getDeviceTypeCounts());
    }
}