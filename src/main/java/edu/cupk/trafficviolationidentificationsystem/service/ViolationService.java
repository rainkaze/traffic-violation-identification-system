package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationQueryDto;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

public interface ViolationService {
    PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto);
}

@Service
class ViolationServiceImpl implements ViolationService {

    private final ViolationMapper violationMapper;

    public ViolationServiceImpl(ViolationMapper violationMapper) {
        this.violationMapper = violationMapper;
    }

    @Override
    public PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto) {
        // 1. 获取符合条件的总记录数
        long totalItems = violationMapper.countViolationsByCriteria(queryDto);

        // 2. 如果总记录数为0，直接返回空结果，避免不必要的查询
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