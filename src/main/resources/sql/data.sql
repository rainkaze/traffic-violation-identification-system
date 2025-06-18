-- 切换到您的数据库上下文
USE `traffic_violation_system`;

-- 清空旧数据（可选，但在重复测试时非常有用）
-- 注意：与创建顺序相反，与删除顺序相同
SET FOREIGN_KEY_CHECKS = 0; -- 临时禁用外键检查，以便能顺利清空
TRUNCATE TABLE `violation_processing_log`;
TRUNCATE TABLE `workflow_assignment_users`;
TRUNCATE TABLE `workflow_node_assignments`;
TRUNCATE TABLE `workflow_triggers`;
TRUNCATE TABLE `workflow_nodes`;
TRUNCATE TABLE `workflows`;
TRUNCATE TABLE `audit_logs`;
TRUNCATE TABLE `enforcement_cases`;
TRUNCATE TABLE `user_districts`;
TRUNCATE TABLE `violations`;
TRUNCATE TABLE `devices`;
TRUNCATE TABLE `traffic_rules`;
TRUNCATE TABLE `districts`;
TRUNCATE TABLE `users`;
SET FOREIGN_KEY_CHECKS = 1; -- 重新启用外键检查

-- =================================================================
-- 1. 填充基础数据 (用户, 辖区, 法规等)
-- =================================================================

-- 填充辖区 (districts)
INSERT INTO `districts` (`district_id`, `district_name`, `city`) VALUES
                                                                     (1, '克拉玛依区', '克拉玛依市'),
                                                                     (2, '独山子区', '克拉玛依市');

-- 填充用户 (users)
-- 我们将创建多个不同角色、分属不同辖区的用户
INSERT INTO `users` (`user_id`, `username`, `password_hash`, `full_name`, `email`, `rank`, `registration_status`) VALUES
                                                                                                                      (1, 'zhangsan', 'hashed_password_placeholder', '张三', 'zhangsan@example.com', '警员', 'APPROVED'),
                                                                                                                      (2, 'lisi', 'hashed_password_placeholder', '李四', 'lisi@example.com', '警员', 'APPROVED'),
                                                                                                                      (3, 'wangwu', 'hashed_password_placeholder', '王五', 'wangwu@example.com', '中队长', 'APPROVED'),
                                                                                                                      (4, 'zhaoliu', 'hashed_password_placeholder', '赵六', 'zhaoliu@example.com', '中队长', 'APPROVED'),
                                                                                                                      (5, 'liuqi', 'hashed_password_placeholder', '刘七', 'liuqi@example.com', '大队长', 'APPROVED'),
                                                                                                                      (6, 'chenba', 'hashed_password_placeholder', '陈八', 'chenba@example.com', '管理员', 'APPROVED'),
                                                                                                                      (7, 'zhoujiu', 'hashed_password_placeholder', '周九', 'zhoujiu@example.com', '警员', 'APPROVED'); -- 这位警员在另一个辖区

-- 关联用户和辖区 (user_districts)
-- 将大部分用户分配到“克拉玛依区”
INSERT INTO `user_districts` (`user_id`, `district_id`) VALUES
                                                            (1, 1), -- 张三（警员）在 克拉玛依区
                                                            (2, 1), -- 李四（警员）在 克拉玛依区
                                                            (3, 1), -- 王五（中队长）在 克拉玛依区
                                                            (4, 1), -- 赵六（中队长）在 克拉玛依区
                                                            (5, 1), -- 刘七（大队长）在 克拉玛依区
                                                            (6, 1), -- 陈八（管理员）在 克拉玛依区
-- 将 周九 分配到“独山子区”
                                                            (7, 2); -- 周九（警员）在 独山子区

-- 填充交通法规 (traffic_rules)
INSERT INTO `traffic_rules` (`rule_id`, `violation_type`, `legal_reference`, `base_fine`, `base_demerit_points`) VALUES
                                                                                                                     (1, '机动车超速行驶超过规定时速20%以上未达到50%的', '《中华人民共和国道路交通安全法》第九十九条', 200.00, 6),
                                                                                                                     (2, '违反禁止标线指示', '《中华人民共和国道路交通安全法》第九十条', 100.00, 1);

-- 填充设备 (devices)
INSERT INTO `devices` (`device_id`, `device_code`, `device_name`, `device_type`, `district_id`, `address`) VALUES
                                                                                                               (101, 'KMQ-CAM-001', '世纪大道摄像头', '高清摄像头', 1, '克拉玛依市世纪大道100号'),
                                                                                                               (102, 'DSZ-RAD-001', '南京路雷达', '雷达测速仪', 2, '独山子区南京路与北京路交叉口');

-- =================================================================
-- 2. 填充核心业务数据 (违法记录)
-- 我们将创建三条有代表性的违法记录
-- =================================================================
INSERT INTO `violations` (`violation_id`, `plate_number`, `violation_time`, `device_id`, `rule_id`, `status`) VALUES
-- 记录1: **【核心测试用例】** 发生在克拉玛依区, 扣6分。这条记录应该精确触发我们下面定义的工作流。
(1001, '新A-88888', '2025-06-15 10:30:00', 101, 1, '待处理'),

-- 记录2: 发生在克拉玛依区, 但只扣1分。这条记录不满足触发条件, 状态将保持“待处理”。
(1002, '新A-66666', '2025-06-15 11:00:00', 101, 2, '待处理'),

-- 记录3: 扣6分, 但发生在独山子区。这条记录也不满足我们为克拉玛依区设定的触发器。
(1003, '新A-77777', '2025-06-15 12:00:00', 102, 1, '待处理');


-- =================================================================
-- 3. 定义一套完整的工作流
-- 这套工作流是专门为“克拉玛依区的严重超速”场景设计的
-- =================================================================

-- 定义工作流主信息 (workflows)
INSERT INTO `workflows` (`workflow_id`, `workflow_name`, `description`, `is_active`, `created_by`) VALUES
    (1, '克拉玛依区-严重超速处理流程', '专门用于处理在克拉玛依区发生的、扣6分及以上的超速违法记录。', TRUE, 6); -- 由管理员(ID=6)创建并激活

-- 定义工作流触发器 (workflow_triggers)
-- 规则: 当 `district_id`是1 (克拉玛依区) 并且 `min_demerit_points`是6时, 启动 `workflow_id`为1 的工作流。
INSERT INTO `workflow_triggers` (`trigger_id`, `workflow_id`, `district_id`, `min_demerit_points`, `priority`, `is_active`) VALUES
    (1, 1, 1, 6, 10, TRUE);

-- 定义工作流的三个节点 (workflow_nodes)
INSERT INTO `workflow_nodes` (`node_id`, `workflow_id`, `node_name`, `step_order`, `completion_rule`) VALUES
                                                                                                          (11, 1, '辖区警员初审', 1, 'ANY_ASSIGNEE'), -- 节点1: 或签
                                                                                                          (12, 1, '指定中队长复核', 2, 'ANY_ASSIGNEE'), -- 节点2: 或签
                                                                                                          (13, 1, '大队长与管理员会签', 3, 'ALL_ASSIGNEES'); -- 节点3: 会签

-- 定义节点1的指派规则 (workflow_node_assignments)
-- 规则: 动态查找事发辖区的“警员”
INSERT INTO `workflow_node_assignments` (`assignment_id`, `node_id`, `assignment_type`, `assigned_rank`) VALUES
    (101, 11, 'DYNAMIC_ROLE_IN_DISTRICT', '警员');

-- 定义节点2的指派规则 (workflow_node_assignments & workflow_assignment_users)
-- 规则: 静态指定用户 王五(ID=3) 和 赵六(ID=4)
INSERT INTO `workflow_node_assignments` (`assignment_id`, `node_id`, `assignment_type`) VALUES
    (102, 12, 'STATIC_USER_LIST');
INSERT INTO `workflow_assignment_users` (`assignment_id`, `user_id`) VALUES
                                                                         (102, 3), -- 王五
                                                                         (102, 4); -- 赵六

-- 定义节点3的指派规则 (workflow_node_assignments & workflow_assignment_users)
-- 规则: 静态指定用户 刘七(ID=5) 和 陈八(ID=6)
INSERT INTO `workflow_node_assignments` (`assignment_id`, `node_id`, `assignment_type`) VALUES
    (103, 13, 'STATIC_USER_LIST');
INSERT INTO `workflow_assignment_users` (`assignment_id`, `user_id`) VALUES
                                                                         (103, 5), -- 刘七
                                                                         (103, 6); -- 陈八

-- =================================================================
-- 数据填充完成
-- =================================================================

-- **说明**:
-- 以下“运行时”表是空的, 这是正常的。
-- 您的应用程序在检测到 violation_id=1001 满足触发器后, 应当:
-- 1. 将 violations 表中 id=1001 的记录 status 更新为 '处理中'。
-- 2. 在 violation_processing_log 表中创建第一条待办记录, 指向 node_id=11。
-- 3. 在用户完成每一步操作后, 您的代码会负责更新或创建 violation_processing_log 中的后续记录。
-- 4. 在整个流程结束后, 在 enforcement_cases 表中创建最终的案件结果。
-- 5. 在用户每次操作时, 在 audit_logs 表中记录操作日志。
SELECT '测试数据填充成功！' AS 'Status';
SELECT '核心测试用例: violation_id = 1001, 它将触发 workflow_id = 1 的流程。' AS 'Test Case';