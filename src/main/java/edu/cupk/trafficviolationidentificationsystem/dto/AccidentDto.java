
package edu.cupk.trafficviolationidentificationsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccidentDto {

    // 设备相关信息
    private Integer deviceId;
    private String deviceCode;
    private String deviceName;
    private String deviceType; // ENUM: 高清摄像头, 雷达测速仪等
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String status; // ENUM: online, offline, warning, maintenance
    private LocalDateTime installedAt;
    private Integer districtId;
    // 关联的违法记录信息
    private Long violationId;
    private String plateNumber;
    private LocalDateTime violationTime;
    private BigDecimal confidenceScore;
    private String violationStatus; // 待处理 / 处理中 / 已处理 / 已归档

    // Getters and Setters
    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getInstalledAt() {
        return installedAt;
    }

    public void setInstalledAt(LocalDateTime installedAt) {
        this.installedAt = installedAt;
    }

    public Long getViolationId() {
        return violationId;
    }

    public void setViolationId(Long violationId) {
        this.violationId = violationId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public LocalDateTime getViolationTime() {
        return violationTime;
    }

    public void setViolationTime(LocalDateTime violationTime) {
        this.violationTime = violationTime;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getViolationStatus() {
        return violationStatus;
    }

    public void setViolationStatus(String violationStatus) {
        this.violationStatus = violationStatus;
    }
}