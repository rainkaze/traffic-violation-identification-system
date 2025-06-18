package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationQueryDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

public interface ViolationService {
    PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto);
}

@Service
class ViolationServiceImpl implements ViolationService {

    private final ViolationMapper violationMapper;
    private final UserMapper userMapper; // 注入 UserMapper

    public ViolationServiceImpl(ViolationMapper violationMapper, UserMapper userMapper) {
        this.violationMapper = violationMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto) {
        // --- 开始：数据范围控制逻辑 ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("已认证的用户在数据库中未找到"));

        // 检查用户是否为管理员
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_管理员"));

        if (!isAdmin) {
            // 如果不是管理员，获取其负责的辖区ID列表
            List<Integer> districtIds = userMapper.findDistrictIdsByUserId(currentUser.getUserId());
            if (districtIds.isEmpty()) {
                // 如果该用户未分配任何辖区，则无权查看任何数据
                return new PageResultDto<>(Collections.emptyList(), 0, 0, queryDto.getPage());
            }
            // 将权限内的辖区ID设置到查询参数中，用于强制过滤
            queryDto.setAccessibleDistrictIds(districtIds);
        }
        // --- 结束：数据范围控制逻辑 ---

        // 1. 获取符合条件的总记录数（对非管理员已自动添加辖区范围）
        long totalItems = violationMapper.countViolationsByCriteria(queryDto);

        // 2. 如果总记录数为0，直接返回空结果
        if (totalItems == 0) {
            return new PageResultDto<>(Collections.emptyList(), 0, 0, queryDto.getPage());
        }

        // 3. 计算 offset
        int offset = (queryDto.getPage() - 1) * queryDto.getPageSize();

        // 4. 获取当前页的数据
        List<ViolationDetailDto> items = violationMapper.findViolationsByCriteria(queryDto, queryDto.getPageSize(), offset);

        // 5. 计算总页数
        long totalPages = (long) Math.ceil((double) totalItems / queryDto.getPageSize());

        // 6. 组装并返回分页结果
        return new PageResultDto<>(items, totalItems, (int) totalPages, queryDto.getPage());
    }
}