-- =================================================================
-- 交通违法处理系统数据库
-- 版本: 终极版 v3.0
-- 特性: 包含基于策略的动态工作流引擎，符合第三范式
-- =================================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `traffic_violation_system`;
-- 切换到该数据库上下文
USE `traffic_violation_system`;

-- =================================================================
-- 准备阶段：安全地删除所有旧表，确保从一个干净的状态开始
-- 注意：删除顺序与创建顺序相反，以避免外键约束导致删除失败
-- =================================================================
DROP TABLE IF EXISTS
    `violation_processing_log`,
    `workflow_assignment_users`,
    `workflow_node_assignments`,
    `workflow_triggers`,
    `workflow_nodes`,
    `workflows`,
    `audit_logs`,
    `enforcement_cases`,
    `user_districts`,
    `violations`,
    `devices`,
    `traffic_rules`,
    `districts`,
    `users`;

-- =================================================================
-- 基础模块 (FOUNDATION MODULE)
-- 包含系统运行所必需的核心实体信息
-- =================================================================

-- 表 1: 用户表 (users)
-- 描述: 存储所有系统用户的基本信息、凭证和角色。
CREATE TABLE `users` (
                         `user_id` INT AUTO_INCREMENT PRIMARY KEY, -- 用户唯一标识符, 主键
                         `username` VARCHAR(50) NOT NULL UNIQUE, -- 用户登录名, 必须唯一
                         `password_hash` VARCHAR(255) NOT NULL, -- 存储加密后的密码哈希值, 绝不存明文密码
                         `full_name` VARCHAR(100) NOT NULL, -- 用户真实姓名
                         `email` VARCHAR(100) NOT NULL UNIQUE, -- 用户电子邮箱, 必须唯一, 用于通知和密码重置
                         `phone_number` VARCHAR(20) UNIQUE, -- 用户手机号, 可选, 唯一
                         `avatar_url` VARCHAR(255) DEFAULT 'https://picsum.photos/id/1005/200/200', -- 用户头像图片的URL, 提供一个默认值
                         `rank` ENUM('警员', '中队长', '大队长', '管理员') NOT NULL, -- 用户职级/角色, 决定其在工作流中的权限
                         `registration_status` ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING', -- 账户注册审批状态 (待审批, 已批准, 已拒绝)
                         `verification_token` VARCHAR(255) NULL, -- 用于邮箱验证或密码重置的临时令牌
                         `verification_token_expires_at` TIMESTAMP NULL, -- 令牌的过期时间
                         `last_login_at` TIMESTAMP NULL, -- 用户最后一次登录的时间
                         `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 记录创建时间
                         `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 记录最后更新时间
);

-- 表 2: 辖区表 (districts)
-- 描述: 存储地理管辖区域信息, 如“克拉玛依区”。
CREATE TABLE `districts` (
                             `district_id` INT AUTO_INCREMENT PRIMARY KEY, -- 辖区唯一标识符, 主键
                             `district_name` VARCHAR(100) NOT NULL UNIQUE, -- 辖区名称, 必须唯一
                             `city` VARCHAR(100) DEFAULT '克拉玛依市', -- 所属城市
                             `notes` TEXT, -- 关于该辖区的备注信息
                             `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- 记录创建时间
);

-- 表 3: 用户-辖区关联表 (user_districts)
-- 描述: 建立用户和辖区之间的多对多关系, 定义一个用户可以管理哪些辖区。
CREATE TABLE `user_districts` (
                                  `user_id` INT NOT NULL, -- 关联到用户表的用户ID
                                  `district_id` INT NOT NULL, -- 关联到辖区表的辖区ID
                                  PRIMARY KEY (`user_id`, `district_id`), -- 联合主键, 防止重复关联
                                  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE, -- 如果用户被删除, 其关联关系也随之删除
                                  FOREIGN KEY (`district_id`) REFERENCES `districts`(`district_id`) ON DELETE CASCADE -- 如果辖区被删除, 其关联关系也随之删除
);

-- 表 4: 交通法规表 (traffic_rules)
-- 描述: 作为一个静态查找表, 存储所有交通违法行为的定义、法规依据及基础处罚标准。
CREATE TABLE `traffic_rules` (
                                 `rule_id` INT AUTO_INCREMENT PRIMARY KEY, -- 法规唯一标识符, 主键
                                 `violation_type` VARCHAR(100) NOT NULL UNIQUE, -- 违法行为的类型, 如“机动车违反规定停放”
                                 `legal_reference` VARCHAR(255), -- 对应的法律条款参考, 如“《道路交通安全法》第XX条”
                                 `base_fine` DECIMAL(10, 2) NOT NULL, -- 基础罚款金额
                                 `base_demerit_points` INT NOT NULL -- 基础扣分值
);

-- 表 5: 设备表 (devices)
-- 描述: 存储所有硬件设备（如摄像头）的详细信息。
CREATE TABLE `devices` (
                           `device_id` INT AUTO_INCREMENT PRIMARY KEY, -- 设备唯一标识符, 主键
                           `device_code` VARCHAR(100) NOT NULL UNIQUE, -- 设备编号, 如 'KMQ-CAM-001', 必须唯一
                           `device_name` VARCHAR(100) NOT NULL, -- 设备的友好名称, 便于识别
                           `device_type` ENUM('高清摄像头', '雷达测速仪', 'AI识别终端', 'GPU推理服务器') NOT NULL, -- 设备的类型
                           `district_id` INT, -- 设备所属的辖区ID, 外键
                           `address` VARCHAR(255) NOT NULL, -- 设备的详细安装地址
                           `latitude` DECIMAL(10, 8), -- 地理纬度坐标
                           `longitude` DECIMAL(11, 8), -- 地理经度坐标
                           `model_name` VARCHAR(100), -- 设备型号
                           `ip_address` VARCHAR(45), -- 设备的IP地址
                           `status` ENUM('online', 'offline', 'warning', 'maintenance') NOT NULL DEFAULT 'offline', -- 设备的当前运行状态
                           `installed_at` DATE, -- 设备的安装日期
                           `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 记录创建时间
                           `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 记录最后更新时间
                           FOREIGN KEY (`district_id`) REFERENCES `districts`(`district_id`) ON DELETE CASCADE -- 如果辖区被删除, 该设备归属信息会一并删除
);

-- 表 6: 违法记录表 (violations)
-- 描述: 核心业务表, 存储所有由设备捕获的原始违法事件记录。
CREATE TABLE `violations` (
                              `violation_id` BIGINT AUTO_INCREMENT PRIMARY KEY, -- 违法记录唯一标识符, 主键
                              `plate_number` VARCHAR(20) NOT NULL, -- 违法车辆的车牌号
                              `violation_time` TIMESTAMP NOT NULL, -- 违法行为发生的确切时间
                              `device_id` INT, -- 捕获该违法的设备ID, 外键
                              `rule_id` INT, -- 对应的交通法规ID, 外键
                              `evidence_image_urls` JSON, -- 存储证据图片的URL列表, 使用JSON格式可以存多个URL
                              `confidence_score` DECIMAL(5, 4), -- AI识别车牌的置信度得分 (例如 0.9987)
                              `status` ENUM('待处理', '处理中', '已处理', '已归档') NOT NULL DEFAULT '待处理', -- 违法记录的宏观生命周期状态
                              `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 记录创建时间
                              FOREIGN KEY (`device_id`) REFERENCES `devices`(`device_id`) ON DELETE SET NULL, -- 如果设备被删除, 此处设为NULL, 保留违法记录
                              FOREIGN KEY (`rule_id`) REFERENCES `traffic_rules`(`rule_id`), -- 关联到具体的交通法规
                              INDEX `idx_plate_number` (`plate_number`), -- 为车牌号创建索引, 加快按车牌查询的速度
                              INDEX `idx_violation_time` (`violation_time`) -- 为违法时间创建索引, 加快按时间范围查询的速度
);

-- 表 7: 执法案件表 (enforcement_cases)
-- 描述: 用于存储工作流处理完成后的最终裁决结果, 与原始违法记录一一对应。
CREATE TABLE `enforcement_cases` (
                                     `case_id` BIGINT AUTO_INCREMENT PRIMARY KEY, -- 案件唯一标识符, 主键
                                     `violation_id` BIGINT NOT NULL UNIQUE, -- 关联的违法记录ID, 必须唯一, 确保一一对应关系
                                     `final_decision` ENUM('处罚确认', '教育警告', '记录作废') NOT NULL, -- 最终处理决定
                                     `final_fine` DECIMAL(10, 2) NULL, -- 最终确认的罚款金额, 可能与基础罚款不同
                                     `final_demerit_points` INT NULL, -- 最终确认的扣分
                                     `decision_reason` TEXT, -- 做出该裁决的理由, 例如“证据不足, 予以作废”
                                     `processed_by_user_id` INT, -- 做出最终裁决的用户ID, 外键
                                     `processed_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 裁决时间
                                     FOREIGN KEY (`violation_id`) REFERENCES `violations`(`violation_id`) ON DELETE CASCADE, -- 如果原始违法记录删除, 案件也随之删除
                                     FOREIGN KEY (`processed_by_user_id`) REFERENCES `users`(`user_id`) ON DELETE SET NULL -- 如果用户被删除, 此处设为NULL, 保留案件结果
);

-- 表 8: 操作日志表 (audit_logs)
-- 描述: 记录系统中所有重要的用户操作, 用于审计、追踪和安全分析。
CREATE TABLE `audit_logs` (
                              `log_id` BIGINT AUTO_INCREMENT PRIMARY KEY, -- 日志唯一标识符, 主键
                              `user_id` INT, -- 执行操作的用户ID, 外键
                              `action_type` VARCHAR(100) NOT NULL, -- 操作类型, 如 'LOGIN', 'CREATE_WORKFLOW', 'UPDATE_VIOLATION'
                              `target_entity_type` VARCHAR(50), -- 操作对象的类型, 如 'WORKFLOW', 'USER'
                              `target_entity_id` VARCHAR(100), -- 操作对象的ID
                              `details` TEXT, -- 操作的详细描述, 可以是JSON格式存储更多上下文信息
                              `client_ip_address` VARCHAR(45), -- 执行操作的客户端IP地址
                              `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 日志记录时间
                              FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE SET NULL -- 如果用户被删除, 操作日志依然保留
);


-- =================================================================
-- 工作流核心模块 (WORKFLOW CORE MODULE)
-- 定义和驱动业务流程的自动化引擎
-- =================================================================

-- 表 9: 工作流主表 (workflows)
-- 描述: 定义一个工作流模板的元数据, 如名称、描述等。
CREATE TABLE `workflows` (
                             `workflow_id` INT AUTO_INCREMENT PRIMARY KEY, -- 工作流唯一标识符, 主键
                             `workflow_name` VARCHAR(100) NOT NULL UNIQUE, -- 工作流的名称, 必须唯一, 如“严重违法处理流程”
                             `description` TEXT, -- 对该工作流的详细描述
                             `is_active` BOOLEAN NOT NULL DEFAULT FALSE, -- 只有激活的工作流才能被触发, 便于“草稿”和“上线”管理
                             `created_by` INT, -- 创建该工作流的管理员ID, 外键
                             FOREIGN KEY (`created_by`) REFERENCES `users`(`user_id`) ON DELETE SET NULL -- 如果创建者被删除, 此处设为NULL
);

-- 表 10: 工作流节点表 (workflow_nodes)
-- 描述: 定义一个工作流中包含的所有步骤(节点)及其处理规则。
CREATE TABLE `workflow_nodes` (
                                  `node_id` INT AUTO_INCREMENT PRIMARY KEY, -- 节点唯一标识符, 主键
                                  `workflow_id` INT NOT NULL, -- 所属的工作流ID, 外键
                                  `node_name` VARCHAR(100) NOT NULL, -- 节点的名称, 如“警员初审”, “领导复核”
                                  `step_order` INT NOT NULL, -- 定义节点在流程中的顺序, 数字越小越靠前
                                  `completion_rule` ENUM('ANY_ASSIGNEE', 'ALL_ASSIGNEES') NOT NULL DEFAULT 'ANY_ASSIGNEE', -- **[核心]** 节点的完成规则: 'ANY_ASSIGNEE'表示“或签”(任一指派人处理即可), 'ALL_ASSIGNEES'表示“会签”(所有指派人必须都处理)
                                  `description` TEXT, -- 对该节点任务的详细描述
                                  FOREIGN KEY (`workflow_id`) REFERENCES `workflows`(`workflow_id`) ON DELETE CASCADE, -- 如果工作流被删除, 其下所有节点也随之删除
                                  UNIQUE KEY `uk_workflow_step` (`workflow_id`, `step_order`) -- 确保一个工作流内的步骤顺序是唯一的
);

-- 表 11: 工作流触发条件表 (workflow_triggers)
-- 描述: “规则引擎”, 定义在什么条件下应该启动哪一个工作流。
CREATE TABLE `workflow_triggers` (
                                     `trigger_id` INT AUTO_INCREMENT PRIMARY KEY, -- 触发器唯一标识符, 主键
                                     `workflow_id` INT NOT NULL, -- 满足条件后要启动的工作流ID, 外键
                                     `district_id` INT NULL, -- **[可选]** 限定此触发器只对特定辖区发生的违法生效
                                     `rule_id` INT NULL, -- **[可选]** 限定此触发器只对特定违法类型生效
                                     `min_fine` DECIMAL(10, 2) NULL, -- 最小罚款金额条件
                                     `max_fine` DECIMAL(10, 2) NULL, -- 最大罚款金额条件
                                     `min_demerit_points` INT NULL, -- 最低扣分条件
                                     `max_demerit_points` INT NULL, -- 最高扣分条件
                                     `priority` INT NOT NULL DEFAULT 0, -- 优先级, 当一条违法记录满足多个触发器时, 优先级高的生效
                                     `is_active` BOOLEAN NOT NULL DEFAULT TRUE, -- 是否激活此触发器
                                     FOREIGN KEY (`workflow_id`) REFERENCES `workflows`(`workflow_id`) ON DELETE CASCADE,
                                     FOREIGN KEY (`district_id`) REFERENCES `districts`(`district_id`),
                                     FOREIGN KEY (`rule_id`) REFERENCES `traffic_rules`(`rule_id`)
);

-- 表 12: 节点指派规则表 (workflow_node_assignments)
-- 描述: **[核心]** 定义一个节点的处理人是如何被找到的。
CREATE TABLE `workflow_node_assignments` (
                                             `assignment_id` INT AUTO_INCREMENT PRIMARY KEY, -- 指派规则唯一标识符, 主键
                                             `node_id` INT NOT NULL, -- 关联的节点ID, 外键
                                             `assignment_type` ENUM('DYNAMIC_ROLE_IN_DISTRICT', 'STATIC_USER_LIST') NOT NULL, -- **[核心]** 指派类型: 'DYNAMIC_ROLE_IN_DISTRICT'表示动态查找事发辖区的特定角色; 'STATIC_USER_LIST'表示使用一个预设的静态人员列表
                                             `assigned_rank` ENUM('警员', '中队长', '大队长', '管理员') NULL, -- 当类型为'DYNAMIC_ROLE_IN_DISTRICT'时, 此字段定义要查找的角色
                                             FOREIGN KEY (`node_id`) REFERENCES `workflow_nodes`(`node_id`) ON DELETE CASCADE
);

-- 表 13: 静态指派用户列表 (workflow_assignment_users)
-- 描述: 当指派类型为'STATIC_USER_LIST'时, 此表存储具体的用户ID列表。
CREATE TABLE `workflow_assignment_users` (
                                             `assignment_id` INT NOT NULL, -- 关联的指派规则ID, 外键
                                             `user_id` INT NOT NULL, -- 被指定的具体用户ID, 外键
                                             PRIMARY KEY (`assignment_id`, `user_id`), -- 联合主键, 防止重复添加
                                             FOREIGN KEY (`assignment_id`) REFERENCES `workflow_node_assignments`(`assignment_id`) ON DELETE CASCADE,
                                             FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE
);

-- 表 14: 违法处理日志表 (violation_processing_log)
-- 描述: “运行时”表, 详细记录每一条违法记录在工作流中流转的每一步状态。
CREATE TABLE `violation_processing_log` (
                                            `log_id` BIGINT AUTO_INCREMENT PRIMARY KEY, -- 日志唯一标识符, 主键
                                            `violation_id` BIGINT NOT NULL, -- 关联的违法记录ID, 外键
                                            `workflow_id` INT NOT NULL, -- 所属的工作流ID, 外键
                                            `node_id` INT NOT NULL, -- 当前所处的节点ID, 外键
                                            `assigned_user_id` INT NULL, -- 具体被指派或“认领”该任务的用户ID, 外键
                                            `status` ENUM('待处理', '处理中', '已完成', '已驳回') NOT NULL DEFAULT '待处理', -- **微观**任务状态, 驱动流程运转
                                            `remarks` TEXT, -- 处理人填写的备注或意见
                                            `arrived_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 任务进入本节点的时间
                                            `processed_at` TIMESTAMP NULL, -- 任务在本节点处理完成的时间
                                            FOREIGN KEY (`violation_id`) REFERENCES `violations`(`violation_id`) ON DELETE CASCADE,
                                            FOREIGN KEY (`workflow_id`) REFERENCES `workflows`(`workflow_id`) ON DELETE CASCADE,
                                            FOREIGN KEY (`node_id`) REFERENCES `workflow_nodes`(`node_id`),
                                            FOREIGN KEY (`assigned_user_id`) REFERENCES `users`(`user_id`) ON DELETE SET NULL
);