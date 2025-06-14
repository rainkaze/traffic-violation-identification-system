package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.UserDto;
import java.util.List;

public interface AdminService {
    List<UserDto> getAllUsers();
    void approveUser(Integer userId);
    void rejectUser(Integer userId);
}