package com.chettra.devflow.dto.sprint;

import com.chettra.devflow.entity.SprintStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class SprintRequest {

    @NotBlank(message = "Sprint name is required")
    @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String goal;

    private SprintStatus status;

    @NotNull(message = "Sprint start date is required")
    private LocalDate startDate;

    @NotNull(message = "Sprint end date is required")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal capacityHours;
}
