package com.chettra.devflow.service;

import com.chettra.devflow.dto.task.TaskRequest;
import com.chettra.devflow.dto.task.TaskResponse;
import com.chettra.devflow.entity.TaskPriority;
import com.chettra.devflow.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskResponse create(Long projectId, TaskRequest request);
    TaskResponse getById(Long projectId, Long taskId);
    Page<TaskResponse> getAll(Long projectId, TaskStatus status, TaskPriority priority, Long assigneeMemberId, Pageable pageable);
    TaskResponse update(Long projectId, Long taskId, TaskRequest request);
}
