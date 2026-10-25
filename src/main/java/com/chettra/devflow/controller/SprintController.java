package com.chettra.devflow.controller;

import com.chettra.devflow.dto.sprint.SprintRequest;
import com.chettra.devflow.dto.sprint.SprintResponse;
import com.chettra.devflow.entity.SprintStatus;
import com.chettra.devflow.service.SprintService;
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
@RequestMapping("/api/v1/projects/{projectId}/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<SprintResponse> create(
            @PathVariable Long projectId,
            @Valid @RequestBody SprintRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sprintService.create(projectId, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'USER')")
    public ResponseEntity<Page<SprintResponse>> getAll(
            @PathVariable Long projectId,
            @RequestParam(required = false) SprintStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(sprintService.getAll(projectId, status, pageable));
    }

    @GetMapping("/{sprintId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'USER')")
    public ResponseEntity<SprintResponse> getById(
            @PathVariable Long projectId,
            @PathVariable Long sprintId) {
        return ResponseEntity.ok(sprintService.getById(projectId, sprintId));
    }

    @PutMapping("/{sprintId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<SprintResponse> update(
            @PathVariable Long projectId,
            @PathVariable Long sprintId,
            @Valid @RequestBody SprintRequest request) {
        return ResponseEntity.ok(sprintService.update(projectId, sprintId, request));
    }
}
