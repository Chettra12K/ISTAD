package com.chettra.devflow.dto.task;

import com.chettra.devflow.entity.TaskPriority;
import com.chettra.devflow.entity.TaskStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Task title must not exceed 255 characters")
    private String title;

    @Size(max = 5000, message = "Task description must not exceed 5000 characters")
    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDate dueDate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Estimated hours must be zero or greater")
    private BigDecimal estimatedHours;

    private Long sprintId;

    private Long assigneeMemberId;
}
