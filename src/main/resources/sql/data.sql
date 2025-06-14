use traffic_violation_system;

-- 1. 插入用户数据
-- 密码均为 'password123'，哈希后
-- Admin user (approved)
INSERT INTO users (username, password_hash, full_name, email, `rank`, registration_status) VALUES
    ('admin', '$2a$10$w0C.s31.nS74aN/bY.WfS.V/j.GqN4jO8t5Zk6E/9S/Q6.C7E1u.m', '系统管理员', 'admin@example.com', '管理员', 'APPROVED');

INSERT INTO users (username, password_hash, full_name, email, `rank`, registration_status) VALUES
    ('officer01', '$2a$10$w0C.s31.nS74aN/bY.WfS.V/j.GqN4jO8t5Zk6E/9S/Q6.C7E1u.m', '李警官', 'officer01@example.com', '警员', 'APPROVED');

INSERT INTO users (username, password_hash, full_name, email, `rank`, registration_status, verification_token) VALUES
    ('pending_officer', '$2a$10$w0C.s31.nS74aN/bY.WfS.V/j.GqN4jO8t5Zk6E/9S/Q6.C7E1u.m', '王申请', 'pending@example.com', '警员', 'PENDING', 'dummy-token-for-testing');



-- 2. 插入地点、法规、设备等
INSERT INTO locations (address, district, latitude, longitude) VALUES
                                                                   ('克拉玛依区世纪大道与友谊路口', '克拉玛依区', 45.5929, 84.8872),
                                                                   ('G30高速K3550+200', '高速路段', 45.4011, 84.9567),
                                                                   ('白碱滩区和平路', '白碱滩区', 45.6883, 85.1215),
                                                                   ('独山子区石化大道与南京路口', '独山子区', 44.3218, 84.8694),
                                                                   ('乌尔禾区迎宾路', '乌尔禾区', 46.1045, 85.6698);

INSERT INTO traffic_rules (violation_type, legal_reference, base_fine, base_demerit_points) VALUES
                                                                                                ('闯红灯', '《道交法》第90条', 200.00, 6),
                                                                                                ('超速行驶', '《道交法》第99条', 150.00, 3),
                                                                                                ('逆行', '《道交法》第90条', 200.00, 3),
                                                                                                ('不按导向车道行驶', '《道交法》第90条', 100.00, 2),
                                                                                                ('违法变道', '《道交法》第90条', 100.00, 1),
                                                                                                ('违章停车', '《道交法》第93条', 50.00, 0);

INSERT INTO devices (device_name, device_type, location_id, status) VALUES
                                                                        ('KMQ-CAM-001', '高清摄像头', 1, 'online'),
                                                                        ('GS-RADAR-002', '雷达测速仪', 2, 'online'),
                                                                        ('BJT-CAM-003', '高清摄像头', 3, 'online'),
                                                                        ('DSZ-CAM-004', '高清摄像头', 4, 'offline'),
                                                                        ('WEH-CAM-005', '高清摄像头', 5, 'online');


-- 3. 插入违法记录
INSERT INTO violations (plate_number, violation_time, location_id, device_id, rule_id, status) VALUES
                                                                                                   ('新K·A12345', '2025-06-09 10:23:45', 1, 1, 1, '待处理'),
                                                                                                   ('新K·B67890', '2025-06-09 09:47:12', 2, 2, 2, '已处理'),
                                                                                                   ('新K·C24681', '2025-06-09 08:15:30', 3, 3, 3, '待处理'),
                                                                                                   ('新K·D35792', '2025-06-08 17:59:18', 4, 4, 4, '已处理'),
                                                                                                   ('新K·E46803', '2025-06-08 15:12:05', 5, 5, 5, '待处理'),
                                                                                                   ('新K·F98765', '2025-06-10 11:05:00', 1, 1, 1, '待处理'),
                                                                                                   ('新K·G54321', '2025-06-11 14:20:10', 2, 2, 2, '待处理');