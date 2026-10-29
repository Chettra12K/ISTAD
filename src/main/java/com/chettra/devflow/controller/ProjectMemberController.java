
package com.chettra.devflow.controller;

import com.chettra.devflow.common.exception.BadRequestException;
import com.chettra.devflow.dto.projectmember.ProjectMemberRequest;
import com.chettra.devflow.dto.projectmember.ProjectMemberResponse;
import com.chettra.devflow.dto.projectmember.ProjectMemberRoleRequest;
import com.chettra.devflow.service.ProjectMemberService;
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
@RequestMapping("/api/v1/projects/{projectId}/members")
@RequiredArgsConstructor
@Validated
public class ProjectMemberController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "role",
            "active",
            "joinedAt"
    );

    private final ProjectMemberService projectMemberService;

    // =========================================================
    // ADD PROJECT MEMBER
    // =========================================================

    @PostMapping
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<ProjectMemberResponse> add(
            @PathVariable @Min(1) Long projectId,
            @Valid @RequestBody ProjectMemberRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectMemberService.add(projectId, request));
    }

    // =========================================================
    // GET PROJECT MEMBERS
    // =========================================================

    @GetMapping
    @PreAuthorize(
            "@projectAuthorizationService.canViewProject(#projectId, authentication)"
    )
    public ResponseEntity<Page<ProjectMemberResponse>> getAll(
            @PathVariable @Min(1) Long projectId,

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be >= 0")
            int page,

            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "Size must be >= 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size,

            @RequestParam(defaultValue = "joinedAt")
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
                projectMemberService.getAll(projectId, pageable)
        );
    }

    // =========================================================
    // GET PROJECT MEMBER BY ID
    // =========================================================

    @GetMapping("/{memberId}")
    @PreAuthorize(
            "@projectAuthorizationService.canViewProject(#projectId, authentication)"
    )
    public ResponseEntity<ProjectMemberResponse> getById(
            @PathVariable @Min(1) Long projectId,
            @PathVariable @Min(1) Long memberId) {

        return ResponseEntity.ok(
                projectMemberService.getById(projectId, memberId)
        );
    }

    // =========================================================
    // UPDATE MEMBER ROLE
    // =========================================================

    @PatchMapping("/{memberId}/role")
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<ProjectMemberResponse> updateRole(
            @PathVariable @Min(1) Long projectId,
            @PathVariable @Min(1) Long memberId,
            @Valid @RequestBody ProjectMemberRoleRequest request) {

        return ResponseEntity.ok(
                projectMemberService.updateRole(projectId, memberId, request)
        );
    }

    // =========================================================
    // REMOVE PROJECT MEMBER
    // =========================================================

    @DeleteMapping("/{memberId}")
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<Void> remove(
            @PathVariable @Min(1) Long projectId,
            @PathVariable @Min(1) Long memberId) {

        projectMemberService.remove(projectId, memberId);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // ASSIGN MEMBER TO TEAM
    // =========================================================

    @PutMapping("/{memberId}/teams/{teamId}")
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<ProjectMemberResponse> addToTeam(
            @PathVariable @Min(1) Long projectId,
            @PathVariable @Min(1) Long memberId,
            @PathVariable @Min(1) Long teamId) {

        return ResponseEntity.ok(
                projectMemberService.addToTeam(
                        projectId,
                        teamId,
                        memberId
                )
        );
    }

    // =========================================================
    // REMOVE MEMBER FROM TEAM
    // =========================================================

    @DeleteMapping("/{memberId}/teams/{teamId}")
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public ResponseEntity<ProjectMemberResponse> removeFromTeam(
            @PathVariable @Min(1) Long projectId,
            @PathVariable @Min(1) Long memberId,
            @PathVariable @Min(1) Long teamId) {

        return ResponseEntity.ok(
                projectMemberService.removeFromTeam(
                        projectId,
                        teamId,
                        memberId
                )
        );
    }

    // =========================================================
    // VALIDATION HELPERS
    // =========================================================

    private void validateSortField(String sortBy) {
        if (sortBy == null || !ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadRequestException(
                    "Invalid project member sort field: " + sortBy
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
