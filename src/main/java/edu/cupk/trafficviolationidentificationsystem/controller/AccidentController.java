package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.AccidentDto;
import edu.cupk.trafficviolationidentificationsystem.service.AccidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 处理与事故相关的API请求的控制器。
 * 主要用于查询和展示事故相关设备的地理位置信息。
 */
@RestController
@RequestMapping("/api/accidents")
public class AccidentController {

    @Autowired
    private AccidentService accidentService;

    /**
     * 根据状态和行政区划名称获取带有位置信息的事故设备列表。
     *
     * @param status 设备状态 (可选参数, 例如 "active", "inactive")。
     * @param districtName 行政区划名称 (可选参数)。
     * @return 返回一个包含设备位置和其他事故信息的 DTO 列表。
     */
    @GetMapping("/devices")
    public List<AccidentDto> getDeviceLocations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String districtName) {
        return accidentService.getDevicesWithLocation(status, districtName);
    }
}