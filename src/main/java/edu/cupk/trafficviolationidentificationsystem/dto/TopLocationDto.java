package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopLocationDto {
    private int rank;
    private String location;
    private String region;
    private int count;
    private String primaryViolationType;
    private double trend; // e.g., 0.10 for +10%, -0.05 for -5%
}