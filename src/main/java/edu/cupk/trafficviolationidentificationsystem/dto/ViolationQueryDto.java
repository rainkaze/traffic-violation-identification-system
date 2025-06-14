package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;

@Data
public class ViolationQueryDto {
    // 筛选条件
    private String plateNumber;
    private String violationType;
    private String status;
    private String yearMonth; // 格式 "YYYY-MM"

    // 分页参数
    private int page = 1;     // 默认请求第1页
    private int pageSize = 10; // 默认每页10条
}