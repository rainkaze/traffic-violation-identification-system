package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViolationDetailDto {
    private Long id;
    private LocalDateTime time;
    private String plate;
    private String type;
    private String location;
    private String device;
    private String status;
    private String district;
    private List<String> evidenceImageUrls;

}