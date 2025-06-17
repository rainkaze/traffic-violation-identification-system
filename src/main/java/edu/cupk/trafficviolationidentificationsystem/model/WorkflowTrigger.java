package edu.cupk.trafficviolationidentificationsystem.model;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class WorkflowTrigger {
    private Integer triggerId;
    private Integer workflowId;
    private Integer districtId;
    private Integer ruleId;
    private BigDecimal minFine;
    private BigDecimal maxFine;
    private Integer minDemeritPoints;
    private Integer maxDemeritPoints;
    private int priority;
    private boolean isActive = true;
}