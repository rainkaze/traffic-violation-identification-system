package edu.cupk.trafficviolationidentificationsystem.repository;

import edu.cupk.trafficviolationidentificationsystem.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByUsername(@Param("username") String username);
    Optional<User> findById(@Param("userId") Integer userId);
    Optional<User> findByEmail(@Param("email") String email);
    Boolean existsByUsername(@Param("username") String username);
    Boolean existsByEmail(@Param("email") String email);
    void save(User user);
    void insertUser(User user);
    void updateUser(User user);
    void deleteUserById(@Param("userId") Integer userId);
    void updateUserStatus(@Param("userId") Integer userId, @Param("status") String status);
    void updateUserPassword(@Param("userId") Integer userId, @Param("passwordHash") String passwordHash);
    List<User> findAll();

    List<String> findDistrictsByUserId(@Param("userId") Integer userId);
    void deleteDistrictsByUserId(@Param("userId") Integer userId);
    void insertUserDistrict(@Param("userId") Integer userId, @Param("districtId") Integer districtId);
    List<Integer> findDistrictIdsByUserId(@Param("userId") Integer userId);

    List<User> findUsersForAssignment(@Param("districtId") Integer districtId);

    //根据userid获取邮箱
    String findEmailByUserId(@Param("userId") Integer userId);

    List<User> searchUsersByKeyword(String keyword);

    List<User> getAllUsers();
}