package com.chettra.devflow.service.impl;

import com.chettra.devflow.common.exception.BadRequestException;
import com.chettra.devflow.common.exception.ConflictException;
import com.chettra.devflow.common.exception.ResourceNotFoundException;
import com.chettra.devflow.dto.sprint.SprintRequest;
import com.chettra.devflow.dto.sprint.SprintResponse;
import com.chettra.devflow.entity.Project;
import com.chettra.devflow.entity.Sprint;
import com.chettra.devflow.entity.SprintStatus;
import com.chettra.devflow.repository.ProjectRepository;
import com.chettra.devflow.repository.SprintRepository;
import com.chettra.devflow.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SprintServiceImpl implements SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;

    @Override
    public SprintResponse create(Long projectId, SprintRequest request) {
        Project project = getProject(projectId);
        validateDates(request);

        String name = normalizeName(request.getName());
        if (sprintRepository.existsByProjectIdAndNameIgnoreCase(projectId, name)) {
            throw new ConflictException("Sprint name already exists in this project");
        }

        Sprint sprint = Sprint.builder()
                .project(project)
                .name(name)
                .goal(request.getGoal())
                .status(request.getStatus() == null ? SprintStatus.PLANNED : request.getStatus())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .capacityHours(request.getCapacityHours())
                .build();

        return toResponse(sprintRepository.save(sprint));
    }

    @Override
    @Transactional(readOnly = true)
    public SprintResponse getById(Long projectId, Long sprintId) {
        return toResponse(getSprint(projectId, sprintId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SprintResponse> getAll(Long projectId, SprintStatus status, Pageable pageable) {
        getProject(projectId);
        Page<Sprint> page = status == null
                ? sprintRepository.findByProjectId(projectId, pageable)
                : sprintRepository.findByProjectIdAndStatus(projectId, status, pageable);
        return page.map(this::toResponse);
    }

    @Override
    public SprintResponse update(Long projectId, Long sprintId, SprintRequest request) {
        Sprint sprint = getSprint(projectId, sprintId);
        validateDates(request);

        String name = normalizeName(request.getName());
        if (!sprint.getName().equalsIgnoreCase(name)
                && sprintRepository.existsByProjectIdAndNameIgnoreCase(projectId, name)) {
            throw new ConflictException("Sprint name already exists in this project");
        }

        if (request.getStatus() == SprintStatus.ACTIVE) {
            Page<Sprint> active = sprintRepository.findByProjectIdAndStatus(projectId, SprintStatus.ACTIVE, Pageable.unpaged());
            boolean anotherActive = active.stream().anyMatch(item -> !item.getId().equals(sprintId));
            if (anotherActive) {
                throw new ConflictException("Only one sprint can be active per project");
            }
        }

        sprint.setName(name);
        sprint.setGoal(request.getGoal());
        if (request.getStatus() != null) sprint.setStatus(request.getStatus());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        sprint.setCapacityHours(request.getCapacityHours());

        return toResponse(sprintRepository.save(sprint));
    }

    private Project getProject(Long projectId) {
        return projectRepository.findByIdAndActiveTrue(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
    }

    private Sprint getSprint(Long projectId, Long sprintId) {
        getProject(projectId);
        return sprintRepository.findByIdAndProjectId(sprintId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + sprintId));
    }

    private void validateDates(SprintRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Sprint end date cannot be before start date");
        }
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Sprint name is required");
        }
        return name.trim().replaceAll("\\s+", " ");
    }

    private SprintResponse toResponse(Sprint sprint) {
        return SprintResponse.builder()
                .id(sprint.getId())
                .projectId(sprint.getProject().getId())
                .projectName(sprint.getProject().getName())
                .name(sprint.getName())
                .goal(sprint.getGoal())
                .status(sprint.getStatus())
                .startDate(sprint.getStartDate())
                .endDate(sprint.getEndDate())
                .capacityHours(sprint.getCapacityHours())
                .createdAt(sprint.getCreatedAt())
                .updatedAt(sprint.getUpdatedAt())
                .build();
    }
}
