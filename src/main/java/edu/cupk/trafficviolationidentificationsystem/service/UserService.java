package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto;
import edu.cupk.trafficviolationidentificationsystem.dto.PasswordChangeDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ProfileUpdateDto;
import edu.cupk.trafficviolationidentificationsystem.dto.UserDto;

public interface UserService {
    UserDto updateProfile(String username, ProfileUpdateDto profileUpdateDto);
    void changePassword(String username, PasswordChangeDto passwordChangeDto);
    PageResultDto<UserDto> getUsers(int page, int size, String keyword);
}