package edu.cupk.trafficviolationidentificationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationDetailDto;
import edu.cupk.trafficviolationidentificationsystem.dto.ViolationTestDto;
import edu.cupk.trafficviolationidentificationsystem.service.ViolationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final ViolationService violationService;
    private final ObjectMapper objectMapper;

    public TestController(ViolationService violationService) {
        this.violationService = violationService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @PostMapping("/submit-violation")
    public ResponseEntity<?> submitViolation(
            @RequestPart("violationData") String violationDataJson,
            @RequestPart("evidenceImage") MultipartFile evidenceImage) {
        try {
            ViolationTestDto violationDto = objectMapper.readValue(violationDataJson, ViolationTestDto.class);
            violationService.createTestViolation(violationDto, evidenceImage);
            return ResponseEntity.ok().body("Test violation received successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing request: " + e.getMessage());
        }
    }

    @GetMapping("/latest-violations")
    public ResponseEntity<List<ViolationDetailDto>> getLatestViolations() {
        return ResponseEntity.ok(violationService.getLatestTestViolations(20)); // 获取最新的20条
    }
}

