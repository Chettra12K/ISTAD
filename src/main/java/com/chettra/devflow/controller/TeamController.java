package com.chettra.devflow.controller;

import com.chettra.devflow.dto.team.TeamRequest;
import com.chettra.devflow.dto.team.TeamResponse;
import com.chettra.devflow.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public TeamResponse create(@PathVariable Long projectId, @Valid @RequestBody TeamRequest request) {
        return teamService.create(projectId, request);
    }

    @GetMapping
    @PreAuthorize(
            "@projectAuthorizationService.canViewProject(#projectId, authentication)"
    )
    public Page<TeamResponse> getAll(
            @PathVariable Long projectId,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return teamService.getAll(projectId, pageable);
    }

    @GetMapping("/{teamId}")
    @PreAuthorize(
            "@projectAuthorizationService.canViewProject(#projectId, authentication)"
    )
    public TeamResponse getById(@PathVariable Long projectId, @PathVariable Long teamId) {
        return teamService.getById(projectId, teamId);
    }

    @PutMapping("/{teamId}")
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public TeamResponse update(
            @PathVariable Long projectId,
            @PathVariable Long teamId,
            @Valid @RequestBody TeamRequest request) {
        return teamService.update(projectId, teamId, request);
    }

    @DeleteMapping("/{teamId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(
            "@projectAuthorizationService.canManageProject(#projectId, authentication)"
    )
    public void delete(@PathVariable Long projectId, @PathVariable Long teamId) {
        teamService.delete(projectId, teamId);
    }
}
