package edu.cupk.trafficviolationidentificationsystem.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
public class User implements UserDetails {
    private Integer userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String rank;
    private String registrationStatus;
    private String verificationToken;
    private LocalDateTime verificationTokenExpiresAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 角色必须以 'ROLE_' 开头，这是Spring Security的惯例
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rank));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !"locked".equalsIgnoreCase(this.registrationStatus);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "APPROVED".equalsIgnoreCase(this.registrationStatus);
    }
}