package com.chettra.devflow.service.impl;

import com.chettra.devflow.common.exception.ResourceNotFoundException;
import com.chettra.devflow.dto.activity.ActivityLogResponse;
import com.chettra.devflow.entity.ActivityLog;
import com.chettra.devflow.entity.Project;
import com.chettra.devflow.entity.User;
import com.chettra.devflow.repository.ActivityLogRepository;
import com.chettra.devflow.repository.ProjectRepository;
import com.chettra.devflow.repository.UserRepository;
import com.chettra.devflow.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public void record(Long projectId, Long userId, String entityType, Long entityId,
                       String action, Map<String, Object> details) {
        Project project = projectRepository.findById(projectId).orElse(null);
        User user = userId == null ? null : userRepository.findById(userId).orElse(null);

        ActivityLog log = ActivityLog.builder()
                .project(project)
                .user(user)
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .details(details)
                .build();

        activityLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> getProjectLogs(Long projectId, Pageable pageable) {
        projectRepository.findByIdAndActiveTrue(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        return activityLogRepository.findByProjectId(projectId, pageable).map(log -> ActivityLogResponse.builder()
                .id(log.getId())
                .projectId(log.getProject() == null ? null : log.getProject().getId())
                .userId(log.getUser() == null ? null : log.getUser().getId())
                .username(log.getUser() == null ? null : log.getUser().getUsername())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .action(log.getAction())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build());
    }
}
