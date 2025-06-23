package edu.cupk.trafficviolationidentificationsystem.entity;

import java.sql.Timestamp;

public class ViolationWarning {
    private Long id;
    private Long violationId;
    private String warningLevel;
    private Timestamp createdAt;
}
