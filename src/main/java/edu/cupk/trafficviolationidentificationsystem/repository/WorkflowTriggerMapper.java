package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.model.WorkflowTrigger;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkflowTriggerMapper {
    void insertTrigger(WorkflowTrigger trigger);
}