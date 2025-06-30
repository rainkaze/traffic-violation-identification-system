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
 * 违法记录相关业务逻辑的服务接口。
 */
public interface ViolationService {
    /**
     * 根据查询条件分页列出违法记录。
     */
    PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto);

    /**
     * 创建一条测试用的违法记录，包含上传证据图片。
     */
    Violation createTestViolation(ViolationTestDto violationDto, MultipartFile evidenceImage);

    /**
     * 获取最新的N条测试违法记录。
     */
    List<ViolationDetailDto> getLatestTestViolations(int limit);

    /**
     * 根据查询条件导出违法记录为指定格式文件。
     */
    byte[] exportViolations(ViolationQueryDto queryDto, String format) throws Exception;

    /**
     * 根据设备ID获取最近的违法记录。
     */
    List<ViolationDetailDto> getRecentViolationsByDeviceId(Integer deviceId);
}

/**
 * ViolationService 的实现类。
 * 【已重构】: 使用 OssFileStorageService 将文件上传至云端。
 */
@Service
class ViolationServiceImpl implements ViolationService {

    private final OssFileStorageService ossFileStorageService; // <-- 【核心修改】依赖新的OSS服务
    private final ViolationMapper violationMapper;
    private final UserMapper userMapper;

    /**
     * 构造函数，注入所有需要的服务和Mapper。
     */
    public ViolationServiceImpl(ViolationMapper violationMapper, UserMapper userMapper, OssFileStorageService ossFileStorageService) {
        this.violationMapper = violationMapper;
        this.userMapper = userMapper;
        this.ossFileStorageService = ossFileStorageService; // <-- 【核心修改】注入新的OSS服务
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

        // --- 分页查询逻辑 (保持不变) ---
        long totalItems = violationMapper.countViolationsByCriteria(queryDto);
        if (totalItems == 0) {
            return new PageResultDto<>(Collections.emptyList(), 0, 0, queryDto.getPage());
        }
        int offset = (queryDto.getPage() - 1) * queryDto.getPageSize();
        List<ViolationDetailDto> items = violationMapper.findViolationsByCriteria(queryDto, queryDto.getPageSize(), offset);
        long totalPages = (long) Math.ceil((double) totalItems / queryDto.getPageSize());

        return new PageResultDto<>(items, totalItems, (int) totalPages, queryDto.getPage());
    }

    /**
     * 【已重构】创建一条测试违法记录。
     * 此方法现在会将证据图片上传到阿里云OSS，并将返回的公开URL存入数据库。
     */
    @Override
    @Transactional
    public Violation createTestViolation(ViolationTestDto violationDto, MultipartFile evidenceImage) {
        // 步骤1：调用OSS服务上传文件，获取可公开访问的URL
        String imageUrl = ossFileStorageService.storeFile(evidenceImage);

        // 步骤2：创建并填充Violation实体
        Violation violation = new Violation();
        violation.setPlateNumber(violationDto.getPlateNumber());
        violation.setViolationTime(violationDto.getViolationTime());
        violation.setDeviceId(violationDto.getDeviceId());
        violation.setRuleId(violationDto.getRuleId());
        violation.setStatus("PENDING");

        // 将返回的OSS URL存入数据库
        violation.setEvidenceImageUrls(List.of(imageUrl));

        // 处理并设置置信度
        if (violationDto.getConfidenceScore() != null) {
            violation.setConfidenceScore(new BigDecimal(violationDto.getConfidenceScore()));
        }

        // 步骤3：将实体插入数据库
        violationMapper.insertTestViolation(violation);

        return violation;
    }

    @Override
    public List<ViolationDetailDto> getLatestTestViolations(int limit) {
        return violationMapper.getLatestTestViolations(limit);
    }

    @Override
    public byte[] exportViolations(ViolationQueryDto queryDto, String format) throws Exception {
        // (此方法逻辑保持不变)
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