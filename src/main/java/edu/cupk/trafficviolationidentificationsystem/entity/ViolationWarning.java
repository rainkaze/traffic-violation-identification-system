package edu.cupk.trafficviolationidentificationsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViolationWarning {
    private Long id;
    private Long violationId;
    private String warningLevel;
    private Timestamp createdAt;
}
