package com.chettra.devflow.service.impl;

import com.chettra.devflow.common.exception.BadRequestException;
import com.chettra.devflow.common.exception.ConflictException;
import com.chettra.devflow.common.exception.ResourceNotFoundException;
import com.chettra.devflow.dto.task.TaskRequest;
import com.chettra.devflow.dto.task.TaskResponse;
import com.chettra.devflow.entity.*;
import com.chettra.devflow.repository.ProjectMemberRepository;
import com.chettra.devflow.repository.ProjectRepository;
import com.chettra.devflow.repository.SprintRepository;
import com.chettra.devflow.repository.TaskRepository;
import com.chettra.devflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final SprintRepository sprintRepository;

    @Override
    public TaskResponse create(Long projectId, TaskRequest request) {
        Project project = getProject(projectId);
        Task task = Task.builder()
                .project(project)
                .taskNumber(nextTaskNumber(projectId))
                .title(normalizeTitle(request.getTitle()))
                .description(request.getDescription())
                .status(request.getStatus() == null ? TaskStatus.TODO : request.getStatus())
                .priority(request.getPriority() == null ? TaskPriority.MEDIUM : request.getPriority())
                .dueDate(request.getDueDate())
                .estimatedHours(request.getEstimatedHours())
                .build();

        applyRelations(task, projectId, request);
        validateDueDate(task);
        return toResponse(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getById(Long projectId, Long taskId) {
        return toResponse(getTask(projectId, taskId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAll(Long projectId, TaskStatus status, TaskPriority priority,
                                     Long assigneeMemberId, Pageable pageable) {
        getProject(projectId);

        Page<Task> tasks;
        if (status != null) {
            tasks = taskRepository.findByProjectIdAndStatus(projectId, status, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findByProjectIdAndPriority(projectId, priority, pageable);
        } else if (assigneeMemberId != null) {
            tasks = taskRepository.findByProjectIdAndAssigneeId(projectId, assigneeMemberId, pageable);
        } else {
            tasks = taskRepository.findByProjectId(projectId, pageable);
        }

        return tasks.map(this::toResponse);
    }

    @Override
    public TaskResponse update(Long projectId, Long taskId, TaskRequest request) {
        Task task = getTask(projectId, taskId);

        if (request.getTitle() != null) {
            task.setTitle(normalizeTitle(request.getTitle()));
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getEstimatedHours() != null) {
            task.setEstimatedHours(request.getEstimatedHours());
        }

        applyRelations(task, projectId, request);
        validateDueDate(task);
        return toResponse(taskRepository.save(task));
    }

    private void applyRelations(Task task, Long projectId, TaskRequest request) {
        if (request.getSprintId() != null) {
            Sprint sprint = sprintRepository.findByIdAndProjectId(request.getSprintId(), projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + request.getSprintId()));
            if (sprint.getStatus() == SprintStatus.COMPLETED) {
                throw new BadRequestException("A task cannot be assigned to a completed sprint");
            }
            task.setSprint(sprint);
        }

        if (request.getAssigneeMemberId() != null) {
            ProjectMember member = projectMemberRepository.findByIdAndProjectId(
                            request.getAssigneeMemberId(), projectId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Project member not found: " + request.getAssigneeMemberId()));
            if (!Boolean.TRUE.equals(member.getActive())) {
                throw new BadRequestException("Cannot assign a task to an inactive project member");
            }
            task.setAssignee(member);
        }
    }

    private Long nextTaskNumber(Long projectId) {
        return taskRepository.findTopByProjectIdOrderByTaskNumberDesc(projectId)
                .map(task -> task.getTaskNumber() + 1)
                .orElse(1L);
    }

    private Project getProject(Long projectId) {
        return projectRepository.findByIdAndActiveTrue(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
    }

    private Task getTask(Long projectId, Long taskId) {
        getProject(projectId);
        return taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
    }

    private void validateDueDate(Task task) {
        if (task.getDueDate() != null && task.getSprint() != null
                && task.getDueDate().isBefore(task.getSprint().getStartDate())) {
            throw new ConflictException("Task due date cannot be before the sprint start date");
        }
        if (task.getDueDate() != null && task.getSprint() != null
                && task.getDueDate().isAfter(task.getSprint().getEndDate())) {
            throw new ConflictException("Task due date cannot be after the sprint end date");
        }
    }

    private String normalizeTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new BadRequestException("Task title is required");
        }
        return title.trim().replaceAll("\\s+", " ");
    }

    private TaskResponse toResponse(Task task) {
        ProjectMember assignee = task.getAssignee();
        Sprint sprint = task.getSprint();

        return TaskResponse.builder()
                .id(task.getId())
                .taskNumber(task.getTaskNumber())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .sprintId(sprint == null ? null : sprint.getId())
                .sprintName(sprint == null ? null : sprint.getName())
                .assigneeMemberId(assignee == null ? null : assignee.getId())
                .assigneeUserId(assignee == null ? null : assignee.getUser().getId())
                .assigneeUsername(assignee == null ? null : assignee.getUser().getUsername())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .estimatedHours(task.getEstimatedHours())
                .completedAt(task.getCompletedAt())
                .version(task.getVersion())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
