package com.chettra.devflow.service;

import com.chettra.devflow.dto.sprint.SprintRequest;
import com.chettra.devflow.dto.sprint.SprintResponse;
import com.chettra.devflow.entity.SprintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SprintService {
    SprintResponse create(Long projectId, SprintRequest request);
    SprintResponse getById(Long projectId, Long sprintId);
    Page<SprintResponse> getAll(Long projectId, SprintStatus status, Pageable pageable);
    SprintResponse update(Long projectId, Long sprintId, SprintRequest request);
}
