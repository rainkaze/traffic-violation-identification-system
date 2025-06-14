package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;

/**
 * 用于接收数据库GROUP BY查询结果的通用DTO
 * 例如：{ "label": "闯红灯", "value": 50 }
 */
@Data
public class CountByLabelDto {
    private String label;
    private Long value;
}