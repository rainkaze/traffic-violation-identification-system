package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByUsername(@Param("username") String username);
    Optional<User> findByEmail(@Param("email") String email);
    Boolean existsByUsername(@Param("username") String username);
    Boolean existsByEmail(@Param("email") String email);
    void save(User user);
    void updateUserStatus(@Param("userId") Integer userId, @Param("status") String status);
    void updateUserPassword(@Param("userId") Integer userId, @Param("passwordHash") String passwordHash);
    List<User> findAll();
}