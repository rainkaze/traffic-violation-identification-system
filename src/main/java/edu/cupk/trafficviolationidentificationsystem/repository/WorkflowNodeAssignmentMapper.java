package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.model.WorkflowNodeAssignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkflowNodeAssignmentMapper {
    void insertAssignment(WorkflowNodeAssignment assignment);
    void insertStaticUser(@Param("assignmentId") Integer assignmentId, @Param("userId") Integer userId);
}