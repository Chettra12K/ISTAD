package com.chettra.devflow.dto.team;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeamResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private Long projectId;
    private String projectName;
    private int memberCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
