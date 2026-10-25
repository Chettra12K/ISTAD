package com.chettra.devflow.controller;

import com.chettra.devflow.dto.comment.CommentRequest;
import com.chettra.devflow.dto.comment.CommentResponse;
import com.chettra.devflow.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'USER')")
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CommentRequest request) {
        String username = jwt.getClaimAsString("preferred_username");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.create(projectId, taskId, username, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'USER')")
    public ResponseEntity<Page<CommentResponse>> getAll(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(commentService.getAll(projectId, taskId, pageable));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.update(projectId, taskId, commentId, request));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<Void> delete(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long commentId) {
        commentService.delete(projectId, taskId, commentId);
        return ResponseEntity.noContent().build();
    }
}
