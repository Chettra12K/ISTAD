package com.chettra.devflow.controller;

import com.chettra.devflow.dto.task.TaskRequest;
import com.chettra.devflow.dto.task.TaskResponse;
import com.chettra.devflow.entity.TaskPriority;
import com.chettra.devflow.entity.TaskStatus;
import com.chettra.devflow.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<TaskResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(projectId, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'USER')")
    public ResponseEntity<Page<TaskResponse>> getAll(
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long assigneeMemberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(taskService.getAll(projectId, status, priority, assigneeMemberId, pageable));
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'USER')")
    public ResponseEntity<TaskResponse> getById(
            @PathVariable Long projectId,
            @PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getById(projectId, taskId));
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.update(projectId, taskId, request));
    }
}
