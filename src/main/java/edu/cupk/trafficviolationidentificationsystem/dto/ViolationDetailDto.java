package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ViolationDetailDto {
    private Long id;
    private LocalDateTime time;
    private String plate;
    private String type;
    private String location;
    private String device;
    private String status;
    private String district;
}