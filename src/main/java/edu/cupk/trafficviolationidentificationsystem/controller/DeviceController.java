package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.annotation.AuditLog;
import edu.cupk.trafficviolationidentificationsystem.dto.*;
import edu.cupk.trafficviolationidentificationsystem.model.Device;
import edu.cupk.trafficviolationidentificationsystem.service.DeviceService;
import edu.cupk.trafficviolationidentificationsystem.service.ViolationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备管理控制器。
 * 提供对系统中监控设备进行增、删、改、查等操作的API接口。
 */
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final ViolationService violationService;

    /**
     * 构造函数，用于注入设备服务和违法记录服务。
     *
     * @param deviceService    设备服务实例。
     * @param violationService 违法记录服务实例。
     */
    public DeviceController(DeviceService deviceService, ViolationService violationService) {
        this.deviceService = deviceService;
        this.violationService = violationService;
    }

    /**
     * 获取所有设备的列表。
     *
     * @return 包含所有设备简要信息的DTO列表。
     */
    @GetMapping
    public ResponseEntity<List<DeviceListDto>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    /**
     * 创建一个新设备。
     *
     * @param deviceDto 包含新设备信息的数据传输对象。
     * @return 创建成功后返回新设备实体及HTTP 201 CREATED状态。
     */
    @PostMapping
    @AuditLog(actionType = "CREATE_DEVICE", targetEntityType = "DEVICE", targetEntityIdExpression = "#result.body.deviceId")
    public ResponseEntity<Device> createDevice(@Valid @RequestBody DeviceUpsertDto deviceDto) {
        if (deviceDto.getStatus() != null) {
            deviceDto.setStatus(deviceDto.getStatus().toUpperCase());
        }
        Device createdDevice = deviceService.createDevice(deviceDto);
        return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
    }

    /**
     * 更新一个已存在的设备信息。
     *
     * @param id        要更新的设备ID。
     * @param deviceDto 包含更新后设备信息的数据传输对象。
     * @return 更新成功后返回更新后的设备实体。
     */
    @PutMapping("/{id}")
    @AuditLog(actionType = "UPDATE_DEVICE", targetEntityType = "DEVICE", targetEntityIdExpression = "#id")
    public ResponseEntity<Device> updateDevice(@PathVariable Integer id, @Valid @RequestBody DeviceUpsertDto deviceDto) {
        if (deviceDto.getStatus() != null) {
            deviceDto.setStatus(deviceDto.getStatus().toUpperCase());
        }
        Device updatedDevice = deviceService.updateDevice(id, deviceDto);
        return ResponseEntity.ok(updatedDevice);
    }

    /**
     * 删除一个设备。
     *
     * @param id 要删除的设备ID。
     * @return 成功时返回HTTP 204 No Content。
     */
    @DeleteMapping("/{id}")
    @AuditLog(actionType = "DELETE_DEVICE", targetEntityType = "DEVICE", targetEntityIdExpression = "#id")
    public ResponseEntity<Void> deleteDevice(@PathVariable Integer id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据ID获取单个设备的详细信息。
     *
     * @param id 设备ID。
     * @return 包含设备详细信息的DTO。
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeviceListDto> getDeviceById(@PathVariable Integer id) {
        return ResponseEntity.ok(deviceService.getDeviceDtoById(id));
    }

    /**
     * 获取所有处于激活状态的摄像头列表。
     *
     * @return 激活状态的摄像头DTO列表。
     */
    @GetMapping("/cameras/active")
    public ResponseEntity<List<MonitoringCameraDto>> getActiveCameras() {
        return ResponseEntity.ok(deviceService.getActiveCameras());
    }

    /**
     * 获取可用的RTSP视频流列表，通常用于提供给AI分析模型。
     *
     * @return 包含设备ID和RTSP地址的DTO列表。
     */
    @GetMapping("/streams")
    public ResponseEntity<List<DeviceStreamDto>> getActiveStreamsForModel() {
        return ResponseEntity.ok(deviceService.getActiveStreams());
    }

    /**
     * 获取按设备状态分组的统计计数。
     *
     * @return 按状态统计的设备数量列表。
     */
    @GetMapping("/statistics/status")
    public ResponseEntity<List<CountByLabelDto>> getDeviceStatusCounts() {
        return ResponseEntity.ok(deviceService.getDeviceStatusCounts());
    }

    /**
     * 获取按设备类型分组的统计计数。
     *
     * @return 按类型统计的设备数量列表。
     */
    @GetMapping("/statistics/type")
    public ResponseEntity<List<CountByLabelDto>> getDeviceTypeCounts() {
        return ResponseEntity.ok(deviceService.getDeviceTypeCounts());
    }

    /**
     * 获取指定设备关联的最近违法记录列表。
     *
     * @param deviceId 设备ID。
     * @return 该设备的违法记录详情列表。
     */
    @GetMapping("/{id}/violations")
    public ResponseEntity<List<ViolationDetailDto>> getRecentViolationsForDevice(@PathVariable("id") Integer deviceId) {
        List<ViolationDetailDto> violations = violationService.getRecentViolationsByDeviceId(deviceId);
        return ResponseEntity.ok(violations);
    }
}