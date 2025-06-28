package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDataDto {

//    用户	角色	状态	上次登录	操作用户	角色	状态	上次登录	操作

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String role;
    private String status;
    private String status1;


    public UserDataDto(Long id, String name, String email, String phone, String avatar, String role, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.role = role;
        this.status = status;
    }
//
//    id: 1,
//    name: '李四',
//    email: 'lisi@example.com',
//    role: '交通执法员',
//    status: '活跃',
//    lastLogin: '2025-06-09 14:20',
//    avatar: 'https://picsum.photos/id/1025/40/40'



//    // 添加全参构造函数
//    public UserDataDto(String name, String email, String phone) {
//        this.name = name;
//        this.email = email;
//        this.phone = phone;
//    }
//
//    // Getter 和 Setter（根据需要添加）
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
//
//    public String getPhone() { return phone; }
//    public void setPhone(String phone) { this.phone = phone; }









//
//    private Integer userId;
//    private String username;
//    private String fullName;
//    private String email;
//    private String rank;
//    private String registrationStatus;
//    private String avatarUrl;
//    private LocalDateTime createdAt;
//
//    public UserDataDto(Integer userId, String username, String fullName, String email, String rank, String registrationStatus, String avatarUrl, LocalDateTime createdAt) {
//        this.userId = userId;
//        this.username = username;
//        this.fullName = fullName;
//        this.email = email;
//        this.rank = rank;
//        this.registrationStatus = registrationStatus;
//        this.avatarUrl = avatarUrl;
//        this.createdAt = createdAt;
//    }




}
