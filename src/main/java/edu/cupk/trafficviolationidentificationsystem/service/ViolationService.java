package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationQueryDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationTestDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.model.Violation;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [合并后] 违法记录相关业务逻辑的服务接口。
 */
public interface ViolationService {
    PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto);
    Violation createTestViolation(ViolationTestDto violationDto, MultipartFile evidenceImage);
    List<ViolationDetailDto> getLatestTestViolations(int limit);
    byte[] exportViolations(ViolationQueryDto queryDto, String format) throws Exception;
    List<ViolationDetailDto> getRecentViolationsByDeviceId(Integer deviceId);
}

/**
 * [合并后] ViolationService 的实现类。
 * 结合了OSS云存储和分页健壮性修复。
 */
@Service
class ViolationServiceImpl implements ViolationService {

    // 1. 依赖于新的OSS服务
    private final OssFileStorageService ossFileStorageService;
    private final ViolationMapper violationMapper;
    private final UserMapper userMapper;

    public ViolationServiceImpl(ViolationMapper violationMapper, UserMapper userMapper, OssFileStorageService ossFileStorageService) {
        this.violationMapper = violationMapper;
        this.userMapper = userMapper;
        this.ossFileStorageService = ossFileStorageService;
    }

    @Override
    public PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto) {
        // --- 数据范围控制逻辑 (保持不变) ---
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userMapper.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("已认证的用户在数据库中未找到"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_管理员"));
        if (!isAdmin) {
            List<Integer> districtIds = userMapper.findDistrictIdsByUserId(currentUser.getUserId());
            if (districtIds.isEmpty()) {
                return new PageResultDto<>(Collections.emptyList(), 0, 0, queryDto.getPage());
            }
            queryDto.setAccessibleDistrictIds(districtIds);
        }

        long totalItems = violationMapper.countViolationsByCriteria(queryDto);
        if (totalItems == 0) {
            return new PageResultDto<>(Collections.emptyList(), 0, 0, queryDto.getPage());
        }

        // 2. 整合版本二的健壮性修复：修正无效的页码
        int page = queryDto.getPage();
        if (page < 1) {
            page = 1; // 将小于1的页码强制修正为1
        }
        int offset = (page - 1) * queryDto.getPageSize();

        List<ViolationDetailDto> items = violationMapper.findViolationsByCriteria(queryDto, queryDto.getPageSize(), offset);
        long totalPages = (long) Math.ceil((double) totalItems / queryDto.getPageSize());

        // 注意：返回给前端的仍然是原始请求的页码
        return new PageResultDto<>(items, totalItems, (int) totalPages, queryDto.getPage());
    }

    /**
     * 3. 保留版本一重构后的方法：
     * 将证据图片上传到阿里云OSS，并处理置信度等信息。
     */
    @Override
    @Transactional
    public Violation createTestViolation(ViolationTestDto violationDto, MultipartFile evidenceImage) {
        String imageUrl = ossFileStorageService.storeFile(evidenceImage);

        Violation violation = new Violation();
        violation.setPlateNumber(violationDto.getPlateNumber());
        violation.setViolationTime(violationDto.getViolationTime());
        violation.setDeviceId(violationDto.getDeviceId());
        violation.setRuleId(violationDto.getRuleId());
        violation.setStatus("PENDING");
        violation.setEvidenceImageUrls(List.of(imageUrl));

        if (violationDto.getConfidenceScore() != null) {
            violation.setConfidenceScore(new BigDecimal(violationDto.getConfidenceScore()));
        }

        violationMapper.insertTestViolation(violation);
        return violation;
    }

    // (以下方法保持不变)
    @Override
    public List<ViolationDetailDto> getLatestTestViolations(int limit) {
        return violationMapper.getLatestTestViolations(limit);
    }

    @Override
    public byte[] exportViolations(ViolationQueryDto queryDto, String format) throws Exception {
        List<ViolationDetailDto> allViolations = violationMapper.findAllViolationsByCriteria(queryDto);
        File file = ResourceUtils.getFile("classpath:reports/report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allViolations);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Admin");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if ("pdf".equalsIgnoreCase(format)) {
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        } else if ("xlsx".equalsIgnoreCase(format)) {
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporter.exportReport();
        } else if ("csv".equalsIgnoreCase(format)) {
            JRCsvExporter exporter = new JRCsvExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
            exporter.exportReport();
        } else {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }
        return outputStream.toByteArray();
    }

    @Override
    public List<ViolationDetailDto> getRecentViolationsByDeviceId(Integer deviceId) {
        return violationMapper.findRecentViolationsByDeviceId(deviceId);
    }
}