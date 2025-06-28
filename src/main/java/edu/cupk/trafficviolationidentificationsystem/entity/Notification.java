package edu.cupk.trafficviolationidentificationsystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    private Long id;
    private Long userId;
    private String message;
    @JsonFormat(pattern = "yyyy/M/d HH:mm:ss")
    private LocalDateTime timestamp;
    @JsonProperty("is_read")
    private Boolean isRead;
}
