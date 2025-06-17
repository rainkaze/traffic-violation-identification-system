package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.dto.WorkflowListDto;
import edu.cupk.trafficviolationidentificationsystem.model.Workflow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List; // 确保导入 java.util.List
import java.util.Optional;

@Mapper
public interface WorkflowMapper {

    /**
     * 插入一个新的工作流主记录
     * @param workflow 工作流对象
     * @return 影响的行数
     */
    int insertWorkflow(Workflow workflow);

    /**
     * 查询所有工作流的摘要信息，用于列表展示
     * (这是之前缺失的方法声明)
     * @return 工作流列表DTO
     */
    List<WorkflowListDto> findAllForList();

    /**
     * 根据工作流ID删除工作流
     * @param workflowId 工作流ID
     * @return 影响的行数
     */
    void deleteById(@Param("workflowId") Integer workflowId);
    /**
     * 更新工作流的激活状态
     * @param workflowId 工作流ID
     * @param isActive 激活状态
     * @return 影响的行数
     */
    void updateActivationStatus(@Param("workflowId") Integer workflowId, @Param("isActive") boolean isActive);


    Optional<Workflow> findById(@Param("workflowId") Integer workflowId);

}