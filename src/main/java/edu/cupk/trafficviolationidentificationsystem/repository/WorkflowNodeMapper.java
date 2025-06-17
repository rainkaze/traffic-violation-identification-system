package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.model.WorkflowNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkflowNodeMapper {

    /**
     * [新增] 添加节点
     * @param node 节点
     */
    void insertNode(WorkflowNode node);

    /**
     * [查询] 根据工作流ID查询节点
     * @param workflowId 工作流ID
     * @return 节点列表
     */
    List<WorkflowNode> findNodesByWorkflowId(@Param("workflowId") Integer workflowId);

    /**
     * [删除] 根据工作流ID删除节点
     * @param workflowId 工作流ID
     */
    void deleteByWorkflowId(@Param("workflowId") Integer workflowId);
}