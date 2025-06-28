package edu.cupk.trafficviolationidentificationsystem.service;

import edu.cupk.trafficviolationidentificationsystem.dto.PageResultDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationQueryDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationTestDto;
import edu.cupk.trafficviolationidentificationsystem.model.User;
import edu.cupk.trafficviolationidentificationsystem.model.Violation;
import edu.cupk.trafficviolationidentificationsystem.repository.UserMapper;
import edu.cupk.trafficviolationidentificationsystem.repository.ViolationMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.DefaultJasperReportsContext; // 引入
import net.sf.jasperreports.engine.fonts.FontFamily; // 引入
public interface ViolationService {
    PageResultDto<ViolationDetailDto> listViolations(ViolationQueryDto queryDto);

    Violation createTestViolation(ViolationTestDto violationDto, MultipartFile evidenceImage);
    List<ViolationDetailDto> getLatestTestViolations(int limit);

    byte[] exportViolations(ViolationQueryDto queryDto, String format) throws Exception;

}

@Service
class ViolationServiceImpl implements ViolationService {
    private final FileStorageService fileStorageService;
    private final ViolationMapper violationMapper;
    private final UserMapper userMapper; // 注入 UserMapper

    public ViolationServiceImpl(ViolationMapper violationMapper, UserMapper userMapper, FileStorageService fileStorageService) {
        this.violationMapper = violationMapper;
        this.userMapper = userMapper;
        this.fileStorageService = fileStorageService; // 初始化
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

    @Override
    @Transactional
    public Violation createTestViolation(ViolationTestDto violationDto, MultipartFile evidenceImage) {
        // 1. 存储图片文件
        String imageUrl = fileStorageService.storeFile(evidenceImage);

        // 2. 创建Violation实体
        Violation violation = new Violation();
        violation.setPlateNumber(violationDto.getPlateNumber());
        violation.setViolationTime(violationDto.getViolationTime());
        violation.setDeviceId(violationDto.getDeviceId());
        violation.setRuleId(violationDto.getRuleId());

        // 将图片URL列表存为JSON格式
        violation.setEvidenceImageUrls(List.of(imageUrl));

        // 3. 插入数据库
        violationMapper.insertTestViolation(violation);
        return violation;
    }

    @Override
    public List<ViolationDetailDto> getLatestTestViolations(int limit) {
        return violationMapper.getLatestTestViolations(limit);
    }

    @Override
    public byte[] exportViolations(ViolationQueryDto queryDto, String format) throws Exception {

        // ======================= 诊断代码 =======================
//        System.out.println("\n--- JasperReports 运行时可用字体 ---");
//        DefaultJasperReportsContext context = (DefaultJasperReportsContext) DefaultJasperReportsContext.getInstance();

//        List<FontFamily> fontFamilies = context.getExtensions(FontFamily.class);

//        if (fontFamilies != null && !fontFamilies.isEmpty()) {
//            for (FontFamily family : fontFamilies) {
//                System.out.println("找到字体家族: " + family.getName());
//            }
//        } else {
//            System.out.println("警告：没有找到任何通过扩展加载的字体！请检查 jasperreports_extension.properties 和 fonts.xml 文件是否存在于 resources 目录下。");
//        }
//        System.out.println("------------------------------------------\n");
        // ======================= 诊断代码结束 =========================

        // 1. 获取所有符合条件的数据（不分页）
        List<ViolationDetailDto> allViolations = violationMapper.findAllViolationsByCriteria(queryDto);

        // 2. 加载报表模板
        File file = ResourceUtils.getFile("classpath:reports/report.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

        // 3. 创建数据源
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allViolations);

        // 4. 填充报表
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Admin");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // 5. 根据格式导出报表
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
}