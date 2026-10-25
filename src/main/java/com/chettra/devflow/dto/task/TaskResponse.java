package com.chettra.devflow.dto.task;

import com.chettra.devflow.entity.TaskPriority;
import com.chettra.devflow.entity.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TaskResponse {
    private Long id;
    private Long taskNumber;
    private Long projectId;
    private String projectName;
    private Long sprintId;
    private String sprintName;
    private Long assigneeMemberId;
    private Long assigneeUserId;
    private String assigneeUsername;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private BigDecimal estimatedHours;
    private LocalDateTime completedAt;
    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
