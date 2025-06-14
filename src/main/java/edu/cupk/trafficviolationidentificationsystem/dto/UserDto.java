package edu.cupk.trafficviolationidentificationsystem.dto;

import edu.cupk.trafficviolationidentificationsystem.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserDto {
    private Integer userId;
    private String username;
    private String fullName;
    private String email;
    private String rank;
    private String registrationStatus;
    private String avatarUrl;
    private LocalDateTime createdAt;

    // 从 User 实体创建 DTO
    public UserDto(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.rank = user.getRank();
        this.registrationStatus = user.getRegistrationStatus();
        this.avatarUrl = user.getAvatarUrl();
        this.createdAt = user.getCreatedAt();
    }
}