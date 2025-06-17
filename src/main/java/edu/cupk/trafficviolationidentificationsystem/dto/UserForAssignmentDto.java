package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserForAssignmentDto {
    private Integer userId;
    private String fullName;
    private String rank;
    private List<String> districts; // 显示该用户的所有辖区
}