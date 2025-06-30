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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 1. 合并后的接口，包含所有方法声明
public interface ViolationService {
    PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto);

    Violation createTestViolation(ViolationTestDto violationDto, MultipartFile evidenceImage);

    List<ViolationDetailDto> getLatestTestViolations(int limit);

    byte[] exportViolations(ViolationQueryDto queryDto, String format) throws Exception;

    List<ViolationDetailDto> getRecentViolationsByDeviceId(Integer deviceId);

}

@Service
class ViolationServiceImpl implements ViolationService {
    // 2. 合并后的依赖，包含FileStorageService
    private final FileStorageService fileStorageService;
    private final ViolationMapper violationMapper;
    private final UserMapper userMapper;

    // 3. 合并后的构造函数
    public ViolationServiceImpl(ViolationMapper violationMapper, UserMapper userMapper, FileStorageService fileStorageService) {
        this.violationMapper = violationMapper;
        this.userMapper = userMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto) {
        // --- 开始：数据范围控制逻辑 ---
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
        // --- 结束：数据范围控制逻辑 ---

        long totalItems = violationMapper.countViolationsByCriteria(queryDto);

        if (totalItems == 0) {
            return new PageResultDto<>(Collections.emptyList(), 0, 0, queryDto.getPage());
        }

        // ===================================================================
        // ======================= 最终核心修改点 ==========================
        //  分页查询的页码通常从0或1开始。为保证健壮性，我们先对页码做预处理。
        //  如果页码小于1（比如前端传来了0），我们将其视为第1页来处理。
        // ===================================================================
        int page = queryDto.getPage();
        if (page < 1) {
            page = 1; // 将小于1的页码强制修正为1
        }
        int offset = (page - 1) * queryDto.getPageSize();


        List<ViolationDetailDto> items = violationMapper.findViolationsByCriteria(queryDto, queryDto.getPageSize(), offset);
        long totalPages = (long) Math.ceil((double) totalItems / queryDto.getPageSize());

        return new PageResultDto<>(items, totalItems, (int) totalPages, queryDto.getPage());
    }

    // 4. 保留版本1中的 createTestViolation 方法
    @Override
    @Transactional
    public Violation createTestViolation(ViolationTestDto violationDto, MultipartFile evidenceImage) {
        String imageUrl = fileStorageService.storeFile(evidenceImage);
        Violation violation = new Violation();
        violation.setPlateNumber(violationDto.getPlateNumber());
        violation.setViolationTime(violationDto.getViolationTime());
        violation.setDeviceId(violationDto.getDeviceId());
        violation.setRuleId(violationDto.getRuleId());
        violation.setEvidenceImageUrls(List.of(imageUrl));
        violationMapper.insertTestViolation(violation);
        return violation;
    }

    // 5. 保留版本1中的 getLatestTestViolations 方法
    @Override
    public List<ViolationDetailDto> getLatestTestViolations(int limit) {
        return violationMapper.getLatestTestViolations(limit);
    }

    // 6. 保留版本1中的 exportViolations 方法
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

    // 7. 添加版本2中的 getRecentViolationsByDeviceId 方法
    @Override
    public List<ViolationDetailDto> getRecentViolationsByDeviceId(Integer deviceId) {
        return violationMapper.findRecentViolationsByDeviceId(deviceId);
    }
}