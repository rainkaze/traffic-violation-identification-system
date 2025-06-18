package edu.cupk.trafficviolationidentificationsystem.dto;
import lombok.Data;
@Data
public class WorkflowTriggerUpsertDto {
    private Integer districtId;
    private Integer ruleId;
    private Integer minDemeritPoints;
    private Integer maxDemeritPoints;
    private int priority;
}