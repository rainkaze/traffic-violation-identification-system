package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.ViolationRecordDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    @GetMapping
    public List<ViolationRecordDto> getAllViolations() {
        // 因为 DTO 中现在有了手写的全参构造函数，所以这里的代码可以被正确编译了
        return Arrays.asList(
                new ViolationRecordDto(1L, "2025-06-09 10:23:45", "新K·A12345", "闯红灯", "克拉玛依区世纪大道与友谊路口", "KMQ-CAM-001", "待处理"),
                new ViolationRecordDto(2L, "2025-06-09 09:47:12", "新K·B67890", "超速行驶", "G30高速K3550+200", "GS-RADAR-002", "已处理"),
                new ViolationRecordDto(3L, "2025-06-09 08:15:30", "新K·C24681", "逆行", "白碱滩区和平路", "BJT-CAM-003", "待处理"),
                new ViolationRecordDto(4L, "2025-06-08 17:59:18", "新K·D35792", "不按导向车道行驶", "独山子区石化大道与南京路口", "DSZ-CAM-004", "已处理"),
                new ViolationRecordDto(5L, "2025-06-08 15:12:05", "新K·E46803", "违法变道", "乌尔禾区迎宾路", "WEH-CAM-005", "待处理")
        );
    }
}