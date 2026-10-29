
package com.chettra.devflow.controller;

import com.chettra.devflow.common.exception.BadRequestException;
import com.chettra.devflow.dto.task.TaskRequest;
import com.chettra.devflow.dto.task.TaskResponse;
import com.chettra.devflow.entity.TaskPriority;
import com.chettra.devflow.entity.TaskStatus;
import com.chettra.devflow.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "title",
            "status",
            "priority",
            "dueDate",
            "taskNumber",
            "createdAt",
            "updatedAt"
    );

    private final TaskService taskService;

    // =========================================================
    // CREATE TASK
    // =========================================================

    @PostMapping
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<TaskResponse> create(
            @PathVariable @Min(1) Long projectId,
            @Valid @RequestBody TaskRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(projectId, request));
    }

    // =========================================================
    // GET TASKS WITH FILTERING AND PAGINATION
    // =========================================================

    @GetMapping
    @PreAuthorize(
            "@projectAuthorizationService.canViewProject(#projectId, authentication)"
    )
    public ResponseEntity<Page<TaskResponse>> getAll(
            @PathVariable @Min(1) Long projectId,

            @RequestParam(required = false)
            TaskStatus status,

            @RequestParam(required = false)
            TaskPriority priority,

            @RequestParam(required = false)
            @Min(value = 1, message = "Assignee member ID must be positive")
            Long assigneeMemberId,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be >= 0")
            int page,

            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "Size must be >= 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "desc")
            String direction) {

        validateSortField(sortBy);

        Sort.Direction sortDirection = parseDirection(direction);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        return ResponseEntity.ok(
                taskService.getAll(
                        projectId,
                        status,
                        priority,
                        assigneeMemberId,
                        pageable
                )
        );
    }

    // =========================================================
    // GET TASK BY ID
    // =========================================================

    @GetMapping("/{taskId}")
    @PreAuthorize(
            "@projectAuthorizationService.canViewProject(#projectId, authentication)"
    )
    public ResponseEntity<TaskResponse> getById(
            @PathVariable @Min(1) Long projectId,
            @PathVariable @Min(1) Long taskId) {

        return ResponseEntity.ok(
                taskService.getById(projectId, taskId)
        );
    }

    // =========================================================
    // UPDATE TASK
    // =========================================================

    @PutMapping("/{taskId}")
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<TaskResponse> update(
            @PathVariable @Min(1) Long projectId,
            @PathVariable @Min(1) Long taskId,
            @Valid @RequestBody TaskRequest request) {

        return ResponseEntity.ok(
                taskService.update(projectId, taskId, request)
        );
    }

    // =========================================================
    // VALIDATION HELPERS
    // =========================================================

    private void validateSortField(String sortBy) {
        if (sortBy == null || !ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid task sort field: " + sortBy
                            + ". Allowed values: "
                            + String.join(", ", ALLOWED_SORT_FIELDS)
            );
        }
    }

    private Sort.Direction parseDirection(String direction) {
        if (direction == null || direction.isBlank()) {
            throw new BadRequestException(
                    "Sort direction is required. Allowed values: asc, desc"
            );
        }

        try {
            return Sort.Direction.valueOf(
                    direction.toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException(
                    "Invalid sort direction: " + direction
                            + ". Allowed values: asc, desc"
            );
        }
    }
}
