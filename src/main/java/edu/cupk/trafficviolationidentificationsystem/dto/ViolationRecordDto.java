package edu.cupk.trafficviolationidentificationsystem.dto;

import java.util.Objects;

// 我们移除了所有 Lombok 注解 (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)
// 并手动实现了所有需要的方法。
public class ViolationRecordDto {

    private long id;
    private String time;
    private String plate;
    private String type;
    private String location;
    private String device;
    private String status;

    // 1. 无参构造函数 (替代 @NoArgsConstructor)
    public ViolationRecordDto() {
    }

    // 2. 全参构造函数 (替代 @AllArgsConstructor)
    public ViolationRecordDto(long id, String time, String plate, String type, String location, String device, String status) {
        this.id = id;
        this.time = time;
        this.plate = plate;
        this.type = type;
        this.location = location;
        this.device = device;
        this.status = status;
    }

    // 3. Getter 和 Setter 方法 (替代 @Data)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // 4. equals(), hashCode(), toString() 方法 (替代 @Data)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViolationRecordDto that = (ViolationRecordDto) o;
        return id == that.id && Objects.equals(time, that.time) && Objects.equals(plate, that.plate) && Objects.equals(type, that.type) && Objects.equals(location, that.location) && Objects.equals(device, that.device) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, plate, type, location, device, status);
    }

    @Override
    public String toString() {
        return "ViolationRecordDto{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", plate='" + plate + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", device='" + device + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}