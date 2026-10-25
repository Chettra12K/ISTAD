package com.chettra.devflow.dto.activity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class ActivityLogResponse {
    private Long id;
    private Long projectId;
    private Long userId;
    private String username;
    private String entityType;
    private Long entityId;
    private String action;
    private Map<String, Object> details;
    private LocalDateTime createdAt;
}
