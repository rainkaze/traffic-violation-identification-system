create database if not exists `traffic_violation_system`;

-- Drop existing tables if they exist to start fresh
DROP TABLE IF EXISTS audit_logs, enforcement_cases, violations, user_roles, devices, locations, traffic_rules, roles, users;


CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE, -- 登录用户名 (警号)
                       password_hash VARCHAR(255) NOT NULL, -- 存储哈希后的密码
                       full_name VARCHAR(100) NOT NULL, -- 用户姓名
                       email VARCHAR(100) NOT NULL UNIQUE, -- 邮箱
                       phone_number VARCHAR(20) UNIQUE, -- 手机号
                       avatar_url VARCHAR(255) DEFAULT 'https://picsum.photos/id/1005/200/200', -- 用户头像图片地址
                       `rank` ENUM('警员', '小队长', '中队长', '大队长', '管理员') NOT NULL, -- <--- 修改这里
                       registration_status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING', -- 新增: 注册状态
                       verification_token VARCHAR(255) NULL, -- 新增: 邮箱验证token
                       verification_token_expires_at TIMESTAMP NULL, -- 新增: token过期时间
                       last_login_at TIMESTAMP NULL, -- 上次登录时间
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE locations (
                           location_id INT AUTO_INCREMENT PRIMARY KEY,
                           address VARCHAR(255) NOT NULL,
                           district VARCHAR(50),
                           latitude DECIMAL(10, 8),
                           longitude DECIMAL(11, 8),
                           INDEX idx_district (district)
);

CREATE TABLE devices (
                         device_id INT AUTO_INCREMENT PRIMARY KEY,
                         device_name VARCHAR(100) NOT NULL UNIQUE,
                         device_type ENUM('高清摄像头', '雷达测速仪', 'AI识别终端', 'GPU推理服务器') NOT NULL,
                         model_name VARCHAR(100),
                         location_id INT,
                         ip_address VARCHAR(45),
                         status ENUM('online', 'offline', 'warning', 'maintenance') NOT NULL DEFAULT 'offline',
                         last_heartbeat_at TIMESTAMP NULL,
                         installed_at DATE,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (location_id) REFERENCES locations(location_id) ON DELETE SET NULL
);

CREATE TABLE traffic_rules (
                               rule_id INT AUTO_INCREMENT PRIMARY KEY,
                               violation_type VARCHAR(100) NOT NULL UNIQUE,
                               legal_reference VARCHAR(255),
                               base_fine DECIMAL(10, 2) NOT NULL,
                               base_demerit_points INT NOT NULL
);

CREATE TABLE violations (
                            violation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            plate_number VARCHAR(20) NOT NULL,
                            violation_time TIMESTAMP NOT NULL,
                            location_id INT,
                            device_id INT,
                            rule_id INT,
                            evidence_image_urls JSON,
                            confidence_score DECIMAL(5, 4),
                            status ENUM('待处理', '处理中', '已处理', '已归档') NOT NULL DEFAULT '待处理',
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (location_id) REFERENCES locations(location_id),
                            FOREIGN KEY (device_id) REFERENCES devices(device_id),
                            FOREIGN KEY (rule_id) REFERENCES traffic_rules(rule_id),
                            INDEX idx_plate_number (plate_number),
                            INDEX idx_violation_time (violation_time)
);


CREATE TABLE audit_logs (
                            log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT,
                            action_type VARCHAR(100) NOT NULL,
                            target_entity_type VARCHAR(50),
                            target_entity_id VARCHAR(100),
                            details TEXT,
                            client_ip_address VARCHAR(45),
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
                            INDEX idx_user_id (user_id),
                            INDEX idx_action_type (action_type)
);