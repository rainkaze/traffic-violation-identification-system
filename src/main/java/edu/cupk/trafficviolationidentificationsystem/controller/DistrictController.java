package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.DistrictDto;
import edu.cupk.trafficviolationidentificationsystem.service.DistrictService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 行政区划控制器。
 * 提供查询系统中所有行政区划信息的API接口。
 */
@RestController
@RequestMapping("/api/districts")
public class DistrictController {

    private final DistrictService districtService;

    /**
     * 构造函数，用于注入行政区划服务。
     *
     * @param districtService 行政区划服务实例。
     */
    public DistrictController(DistrictService districtService) {
        this.districtService = districtService;
    }

    /**
     * 获取所有行政区划的列表。
     *
     * @return 包含所有行政区划信息的DTO列表。
     */
    @GetMapping
    public ResponseEntity<List<DistrictDto>> getAllDistricts() {
        return ResponseEntity.ok(districtService.getAllDistricts());
    }
}