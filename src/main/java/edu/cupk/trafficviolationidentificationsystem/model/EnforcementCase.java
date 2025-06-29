package edu.cupk.trafficviolationidentificationsystem.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EnforcementCase {
    private Long caseId;
    private Long violationId;
    private String finalDecision;
    private BigDecimal finalFine;
    private Integer finalDemeritPoints;
    private String decisionReason;
    private Integer processedByUserId;
    private LocalDateTime processedAt;
}