package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.model.WorkflowTrigger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkflowTriggerMapper {
    /**
     * [新增] 添加工作流触发器
     * @param trigger 工作流触发器
     */
    void insertTrigger(WorkflowTrigger trigger);

    /**
     * [查询] 根据工作流ID查询工作流触发器
     * @param workflowId 工作流ID
     */
    WorkflowTrigger findByWorkflowId(@Param("workflowId") Integer workflowId);

    /**
     * [删除] 根据工作流ID删除工作流触发器
     * @param workflowId 工作流ID
     */
    void deleteByWorkflowId(@Param("workflowId") Integer workflowId);

    List<WorkflowTrigger> findMatchingTriggers(@Param("districtId") Integer districtId, @Param("ruleId") Integer ruleId, @Param("demeritPoints") Integer demeritPoints);

}