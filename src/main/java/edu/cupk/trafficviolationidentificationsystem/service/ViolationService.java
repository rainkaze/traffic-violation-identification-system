package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationQueryDto;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ViolationService {
    PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto);
}

@Service
class ViolationServiceImpl implements ViolationService {

    @Autowired
    private ViolationMapper violationMapper;

    @Override
    public PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto) {
        // 1. 获取符合条件的总记录数
        long totalItems = violationMapper.countViolationsByCriteria(queryDto);

        // 2. 如果总记录数为0，直接返回空结果
        if (totalItems == 0) {
            return new PageResultDto<>(List.of(), 0, 0, queryDto.getPage());
        }

        // 3. 获取当前页的数据
        List<ViolationDetailDto> items = violationMapper.findViolationsByCriteria(queryDto);

        // 4. 计算总页数
        int totalPages = (int) Math.ceil((double) totalItems / queryDto.getPageSize());

        // 5. 组装并返回分页结果
        return new PageResultDto<>(items, totalItems, totalPages, queryDto.getPage());
    }
}