package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.model.WorkflowNodeAssignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkflowNodeAssignmentMapper {
    /**
     * [新增] 新增指派规则
     * @param assignment 指派规则对象
     */
    void insertAssignment(WorkflowNodeAssignment assignment);

    /**
     * [查找] 根据指派ID查找所有动态分配的用户ID
     * @param assignmentId 指派ID
     * @return 用户ID列表
     */
    void insertStaticUser(@Param("assignmentId") Integer assignmentId, @Param("userId") Integer userId);

    /**
     * [新增] 根据节点ID查找指派规则
     * @param nodeId 节点ID
     * @return 指派规则对象
     */
    WorkflowNodeAssignment findByNodeId(@Param("nodeId") Integer nodeId);

    /**
     * [查找] 根据指派ID查找所有静态分配的用户ID
     * @param assignmentId 指派ID
     * @return 用户ID列表
     */
    List<Integer> findStaticUserIdsByAssignmentId(@Param("assignmentId") Integer assignmentId);

}