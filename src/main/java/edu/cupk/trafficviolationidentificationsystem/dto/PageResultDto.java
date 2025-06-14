package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResultDto<T> {
    private List<T> items;      // 当前页的数据列表
    private long totalItems;    // 总记录数
    private int totalPages;     // 总页数
    private int currentPage;    // 当前页码
}