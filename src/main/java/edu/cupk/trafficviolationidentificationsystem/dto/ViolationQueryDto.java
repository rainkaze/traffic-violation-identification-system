package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class ViolationQueryDto {
    // 筛选条件
    private String plateNumber;
    private String violationType;
    private String status;
    private String yearMonth; // 格式 "YYYY-MM"
    private Integer districtId; // 新增：用于前端的辖区筛选

    // 内部权限控制字段，不暴露给API
    private List<Integer> accessibleDistrictIds;

    // 分页参数
    private int page = 1;
    private int pageSize = 10;
}