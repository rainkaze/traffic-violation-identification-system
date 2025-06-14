create database if not exists `traffic_violation_system`;

CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE, -- 登录用户名
                       password_hash VARCHAR(255) NOT NULL, -- 存储哈希后的密码，确保安全
                       full_name VARCHAR(100) NOT NULL, -- 用户姓名，如“克拉玛依交警”
                       email VARCHAR(100) UNIQUE, -- 邮箱
                       phone_number VARCHAR(20) UNIQUE, -- 手机号
                       avatar_url VARCHAR(255), -- 用户头像图片地址
                       status ENUM('active', 'disabled', 'locked') NOT NULL DEFAULT 'active', -- 用户状态
                       last_login_at TIMESTAMP NULL, -- 上次登录时间
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 记录创建时间
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 记录更新时间
);

-- 设计说明
-- * 存储所有可以登录本系统的用户信息。
-- * `password_hash` 字段强调了密码绝不能以明文存储。
-- * `status` 字段用于管理用户账户的生命周期，例如禁用已离职的员工账户。


CREATE TABLE roles (
                       role_id INT AUTO_INCREMENT PRIMARY KEY,
                       role_name VARCHAR(50) NOT NULL UNIQUE, -- 角色名称，如“系统管理员”, “交通执法员”
                       description TEXT, -- 角色描述
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 设计说明
-- * 定义系统中有哪些角色，与权限进行解耦。
-- * 便于在“角色权限”设置页面进行管理。


CREATE TABLE user_roles (
                            user_id INT NOT NULL,
                            role_id INT NOT NULL,
                            PRIMARY KEY (user_id, role_id), -- 复合主键
                            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

-- 设计说明
-- * 这是典型的多对多关系实现方式，一个用户可以有多个角色，一个角色也可以分配给多个用户。


CREATE TABLE locations (
                           location_id INT AUTO_INCREMENT PRIMARY KEY,
                           address VARCHAR(255) NOT NULL, -- 详细地址，如“世纪大道与友谊路口”
                           district VARCHAR(50), -- 所属区域，如“克拉玛依区”
                           latitude DECIMAL(10, 8), -- 纬度
                           longitude DECIMAL(11, 8), -- 经度
                           INDEX idx_district (district) -- 为按区域筛选建立索引
);

-- 设计说明
-- * 将地点信息独立出来，可以被 `devices` 和 `violations` 表复用。
-- * 便于未来进行基于地理位置的分析（如地图热力图）。


CREATE TABLE devices (
                         device_id INT AUTO_INCREMENT PRIMARY KEY,
                         device_name VARCHAR(100) NOT NULL UNIQUE, -- 设备唯一标识符，如 “KMQ-CAM-001”
                         device_type ENUM('高清摄像头', '雷达测速仪', 'AI识别终端', 'GPU推理服务器') NOT NULL,
                         model_name VARCHAR(100), -- 设备型号，如 “YOLOv5s-ONNX”
                         location_id INT, -- 外键，关联到地点表
                         ip_address VARCHAR(45), -- 设备的IP地址
                         status ENUM('online', 'offline', 'warning', 'maintenance') NOT NULL DEFAULT 'offline', -- 设备状态
                         last_heartbeat_at TIMESTAMP NULL, -- 最后一次心跳时间，用于判断在线状态
                         installed_at DATE, -- 安装日期
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (location_id) REFERENCES locations(location_id) ON DELETE SET NULL
);

-- 设计说明
-- * 满足 `DevicesView.vue` 和 `MonitoringView.vue` 的所有展示需求。
-- * `status` 和 `last_heartbeat_at` 是监控设备状态的关键字段。


CREATE TABLE traffic_rules (
                               rule_id INT AUTO_INCREMENT PRIMARY KEY,
                               violation_type VARCHAR(100) NOT NULL UNIQUE, -- 违法行为类型，如“闯红灯”
                               legal_reference VARCHAR(255), -- 法律条款依据，如“《道交法》第26条”
                               base_fine DECIMAL(10, 2) NOT NULL, -- 基准罚款金额
                               base_demerit_points INT NOT NULL -- 基准记分
);

-- 设计说明
-- * 对应 `SettingsView.vue` 中的“法规库管理”。
-- * 将处罚标准结构化，便于“执法流程”中的处罚建议生成。


CREATE TABLE violations (
                            violation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            plate_number VARCHAR(20) NOT NULL, -- 车牌号
                            violation_time TIMESTAMP NOT NULL, -- 违法发生时间
                            location_id INT, -- 违法地点，关联地点表
                            device_id INT, -- 抓拍设备，关联设备表
                            rule_id INT, -- 违反的规则，关联法规库
                            evidence_image_urls JSON, -- 证据图片URL列表，使用JSON格式存储
                            confidence_score DECIMAL(5, 4), -- AI识别的置信度
                            status ENUM('待处理', '处理中', '已处理', '已归档') NOT NULL DEFAULT '待处理',
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            FOREIGN KEY (location_id) REFERENCES locations(location_id),
                            FOREIGN KEY (device_id) REFERENCES devices(device_id),
                            FOREIGN KEY (rule_id) REFERENCES traffic_rules(rule_id),

                            INDEX idx_plate_number (plate_number), -- 为车牌号搜索建立索引
                            INDEX idx_violation_time (violation_time) -- 为按时间筛选建立索引
);

-- 设计说明
-- * 这是系统的核心表，记录了每一次违法事件的快照信息。
-- * `evidence_image_urls` 使用 JSON 类型，可以灵活存储一个或多个图片地址。
-- * `status` 是一个高级别的状态，用于在列表页（如 `ViolationsView.vue`）快速筛选。详细流程状态见 `enforcement_cases`。


CREATE TABLE enforcement_cases (
                                   case_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   violation_id BIGINT NOT NULL UNIQUE, -- 关联到具体的违法记录，一对一关系
                                   case_status ENUM('待审查', '审查通过', '审查驳回', '待审批', '审批通过', '已通知', '已完结') NOT NULL,
                                   assigned_officer_id INT, -- 负责处理该案件的执法员
                                   final_fine_amount DECIMAL(10, 2), -- 最终确定的罚款金额
                                   final_demerit_points INT, -- 最终确定的记分
                                   officer_notes TEXT, -- 执法员备注
                                   approval_notes TEXT, -- 审批意见
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                   FOREIGN KEY (violation_id) REFERENCES violations(violation_id) ON DELETE CASCADE,
                                   FOREIGN KEY (assigned_officer_id) REFERENCES users(user_id)
);

-- 设计说明
-- * 将执法流程从 `violations` 表中分离出来，使 `violations` 表保持稳定，而 `enforcement_cases` 表专门处理变化的流程状态。
-- * 满足 `EnforcementView.vue` 中展示的复杂审批流程。



CREATE TABLE audit_logs (
                            log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT, -- 操作人
                            action_type VARCHAR(100) NOT NULL, -- 操作类型，如 "用户登录", "更新设备", "处理违法记录"
                            target_entity_type VARCHAR(50), -- 操作对象的类型，如 "device", "violation"
                            target_entity_id VARCHAR(100), -- 操作对象的ID
                            details TEXT, -- 操作详情，可存储JSON格式的变更前后数据
                            client_ip_address VARCHAR(45), -- 操作时的IP地址
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
                            INDEX idx_user_id (user_id),
                            INDEX idx_action_type (action_type)
);

-- 设计说明
-- * 对应 `SettingsView.vue` 中的“操作日志”功能。
-- * 记录所有关键操作，用于安全审计和问题追溯，是企业级系统的标配。