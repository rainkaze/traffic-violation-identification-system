package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDistrictAssignmentDto {
    private List<Integer> districtIds;
}