package com.chettra.devflow.controller;

import com.chettra.devflow.dto.projectmember.ProjectMemberRequest;
import com.chettra.devflow.dto.projectmember.ProjectMemberResponse;
import com.chettra.devflow.dto.projectmember.ProjectMemberRoleRequest;
import com.chettra.devflow.service.ProjectMemberService;
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
@RequestMapping("/api/v1/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @PostMapping
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<ProjectMemberResponse> add(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectMemberService.add(projectId, request));
    }

    @GetMapping
    @PreAuthorize(
            "@projectAuthorizationService.canViewProject(#projectId, authentication)"
    )
    public ResponseEntity<Page<ProjectMemberResponse>> getAll(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "joinedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(projectMemberService.getAll(projectId, pageable));
    }

    @GetMapping("/{memberId}")
    @PreAuthorize(
            "@projectAuthorizationService.canViewProject(#projectId, authentication)"
    )
    public ResponseEntity<ProjectMemberResponse> getById(
            @PathVariable Long projectId,
            @PathVariable Long memberId) {
        return ResponseEntity.ok(projectMemberService.getById(projectId, memberId));
    }

    @PatchMapping("/{memberId}/role")
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<ProjectMemberResponse> updateRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @Valid @RequestBody ProjectMemberRoleRequest request) {
        return ResponseEntity.ok(projectMemberService.updateRole(projectId, memberId, request));
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<Void> remove(
            @PathVariable Long projectId,
            @PathVariable Long memberId) {
        projectMemberService.remove(projectId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{memberId}/teams/{teamId}")
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<ProjectMemberResponse> addToTeam(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @PathVariable Long teamId) {
        return ResponseEntity.ok(projectMemberService.addToTeam(projectId, teamId, memberId));
    }

    @DeleteMapping("/{memberId}/teams/{teamId}")
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<ProjectMemberResponse> removeFromTeam(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @PathVariable Long teamId) {
        return ResponseEntity.ok(projectMemberService.removeFromTeam(projectId, teamId, memberId));
    }
}
