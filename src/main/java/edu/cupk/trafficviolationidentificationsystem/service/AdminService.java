package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.UserDto;
import edu.cupk.trafficviolationidentificationsystem.dto.UserUpsertDto;
import jakarta.validation.Valid; // 导入 @Valid
import java.util.List;

public interface AdminService {
    List<UserDto> getAllUsers();
    void approveUser(Integer userId);
    void rejectUser(Integer userId);
    UserDto createUser(@Valid UserUpsertDto userUpsertDto);
    UserDto updateUser(Integer userId, @Valid UserUpsertDto userUpsertDto);
    void deleteUser(Integer userId);

    void updateUserDistricts(Integer userId, List<Integer> districtIds);
}