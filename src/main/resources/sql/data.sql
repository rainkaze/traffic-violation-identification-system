use traffic_violation_system;

-- 1. 插入用户数据
-- 密码均为 'password123'，哈希后
-- Admin user (approved)
INSERT INTO users (username, password_hash, full_name, email, `rank`, registration_status) VALUES
                                                                                               ('admin', '$2a$10$w0C.s31.nS74aN/bY.WfS.V/j.GqN4jO8t5Zk6E/9S/Q6.C7E1u.m', '系统管理员', 'admin@example.com', '管理员', 'APPROVED'),
                                                                                               ('officer01', '$2a$10$w0C.s31.nS74aN/bY.WfS.V/j.GqN4jO8t5Zk6E/9S/Q6.C7E1u.m', '李警官', 'officer01@example.com', '警员', 'APPROVED'),
                                                                                               ('pending_officer', '$2a$10$w0C.s31.nS74aN/bY.WfS.V/j.GqN4jO8t5Zk6E/9S/Q6.C7E1u.m', '王申请', 'pending@example.com', '警员', 'PENDING');

-- 2. 插入地点、法规、设备等
INSERT INTO locations (location_id, address, district, latitude, longitude) VALUES
                                                                                (1, '克拉玛依区世纪大道与友谊路口', '克拉玛依区', 45.5929, 84.8872),
                                                                                (2, 'G30高速K3550+200', '高速路段', 45.4011, 84.9567),
                                                                                (3, '白碱滩区和平路', '白碱滩区', 45.6883, 85.1215),
                                                                                (4, '独山子区石化大道与南京路口', '独山子区', 44.3218, 84.8694),
                                                                                (5, '乌尔禾区迎宾路', '乌尔禾区', 46.1045, 85.6698)
    ON DUPLICATE KEY UPDATE address=VALUES(address);

INSERT INTO traffic_rules (rule_id, violation_type, legal_reference, base_fine, base_demerit_points) VALUES
                                                                                                         (1, '闯红灯', '《道交法》第90条', 200.00, 6),
                                                                                                         (2, '超速行驶', '《道交法》第99条', 150.00, 3),
                                                                                                         (3, '逆行', '《道交法》第90条', 200.00, 3),
                                                                                                         (4, '不按导向车道行驶', '《道交法》第90条', 100.00, 2),
                                                                                                         (5, '违法变道', '《道交法》第90条', 100.00, 1),
                                                                                                         (6, '违章停车', '《道交法》第93条', 50.00, 0)
    ON DUPLICATE KEY UPDATE violation_type=VALUES(violation_type);

INSERT INTO devices (device_id, device_name, device_type, location_id, status) VALUES
                                                                                   (1, 'KMQ-CAM-001', '高清摄像头', 1, 'online'),
                                                                                   (2, 'GS-RADAR-002', '雷达测速仪', 2, 'online'),
                                                                                   (3, 'BJT-CAM-003', '高清摄像头', 3, 'online'),
                                                                                   (4, 'DSZ-CAM-004', '高清摄像头', 4, 'offline'),
                                                                                   (5, 'WEH-CAM-005', '高清摄像头', 5, 'online')
    ON DUPLICATE KEY UPDATE device_name=VALUES(device_name);

-- 3. 插入20条更多样化的违法记录
INSERT INTO violations (plate_number, violation_time, location_id, device_id, rule_id, status) VALUES
                                                                                                   ('新K·A12345', '2025-06-09 10:23:45', 1, 1, 1, '待处理'),
                                                                                                   ('新K·B67890', '2025-06-09 09:47:12', 2, 2, 2, '已处理'),
                                                                                                   ('新K·C24681', '2025-06-08 08:15:30', 3, 3, 3, '待处理'),
                                                                                                   ('新K·D35792', '2025-06-08 17:59:18', 4, 4, 4, '已处理'),
                                                                                                   ('新K·E46803', '2025-06-07 15:12:05', 5, 5, 5, '待处理'),
                                                                                                   ('新K·F98765', '2025-05-20 11:05:00', 1, 1, 1, '已处理'),
                                                                                                   ('新K·G54321', '2025-05-18 14:20:10', 2, 2, 2, '已归档'),
                                                                                                   ('新K·H11223', '2025-05-15 18:30:00', 3, 3, 5, '待处理'),
                                                                                                   ('新K·I33445', '2025-06-01 20:05:50', 1, 1, 6, '已处理'),
                                                                                                   ('新K·J55667', '2025-06-02 07:45:15', 4, 4, 4, '待处理'),
                                                                                                   ('新K·K77889', '2025-04-10 12:00:00', 2, 2, 2, '已归档'),
                                                                                                   ('新K·L99001', '2025-04-12 16:23:45', 1, 1, 1, '已处理'),
                                                                                                   ('新K·M23456', '2025-06-10 09:11:11', 5, 5, 3, '待处理'),
                                                                                                   ('新K·N67890', '2025-06-03 13:13:13', 3, 3, 4, '处理中'),
                                                                                                   ('新K·P11111', '2025-05-25 22:22:22', 2, 2, 2, '已处理'),
                                                                                                   ('新K·Q22222', '2025-06-11 00:33:44', 1, 1, 1, '待处理'),
                                                                                                   ('新K·R33333', '2025-05-01 10:10:10', 4, 4, 6, '已归档'),
                                                                                                   ('新K·S44444', '2025-05-02 11:12:13', 5, 5, 5, '已处理'),
                                                                                                   ('新K·T55555', '2025-05-03 14:15:16', 1, 1, 4, '待处理'),
                                                                                                   ('新K·U66666', '2025-04-30 19:20:21', 3, 3, 3, '已归档');
