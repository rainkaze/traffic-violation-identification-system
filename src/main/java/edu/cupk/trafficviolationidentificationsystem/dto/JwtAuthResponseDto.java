package edu.cupk.trafficviolationidentificationsystem.dto;

import edu.cupk.trafficviolationidentificationsystem.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponseDto {
    private String token;
    private String tokenType = "Bearer";
    private UserDto user;

    public JwtAuthResponseDto(String token, User user) {
        this.token = token;
        this.user = new UserDto(user);
    }
}