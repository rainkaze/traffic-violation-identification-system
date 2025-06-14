package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String rank; // e.g., "警员"
}