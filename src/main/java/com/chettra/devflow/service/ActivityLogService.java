package com.chettra.devflow.service;

import com.chettra.devflow.dto.activity.ActivityLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ActivityLogService {
    void record(Long projectId, Long userId, String entityType, Long entityId, String action, Map<String, Object> details);
    Page<ActivityLogResponse> getProjectLogs(Long projectId, Pageable pageable);
}
