package com.chettra.devflow.dto.sprint;

import com.chettra.devflow.entity.SprintStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class SprintResponse {
    private Long id;
    private Long projectId;
    private String projectName;
    private String name;
    private String goal;
    private SprintStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal capacityHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
