package edu.cupk.trafficviolationidentificationsystem.controller;

import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationQueryDto;
import edu.cupk.trafficviolationidentificationsystem.service.ViolationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    private final ViolationService violationService;

    public ViolationController(ViolationService violationService) {
        this.violationService = violationService;
    }

    /**
     * 获取违法记录列表（支持筛选和分页）
     * @param queryDto Spring Boot 会自动将请求参数绑定到 DTO 对象上
     * @return 分页后的违法记录数据
     */
    @GetMapping
    public ResponseEntity<PageResultDto<ViolationDetailDto>> getViolations(ViolationQueryDto queryDto) {
        PageResultDto<ViolationDetailDto> result = violationService.listViolations(queryDto);
        return ResponseEntity.ok(result);
    }
}