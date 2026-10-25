package com.chettra.devflow.service;

import com.chettra.devflow.common.exception.ConflictException;
import com.chettra.devflow.common.exception.ResourceNotFoundException;
import com.chettra.devflow.dto.team.TeamRequest;
import com.chettra.devflow.dto.team.TeamResponse;
import com.chettra.devflow.entity.Project;
import com.chettra.devflow.entity.Team;
import com.chettra.devflow.repository.ProjectRepository;
import com.chettra.devflow.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public TeamResponse create(Long projectId, TeamRequest request) {
        Project project = getProject(projectId);
        String name = normalize(request.getName());
        if (teamRepository.existsByProjectIdAndNameIgnoreCaseAndActiveTrue(projectId, name)) {
            throw new ConflictException("An active team with this name already exists in the project: " + name);
        }
        Team team = Team.builder().name(name).description(request.getDescription()).project(project).active(true).build();
        return toResponse(teamRepository.save(team));
    }

    @Override
    public TeamResponse getById(Long projectId, Long teamId) {
        return toResponse(teamRepository.findByIdAndProjectId(teamId, projectId).filter(Team::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId)));
    }

    @Override
    public Page<TeamResponse> getAll(Long projectId, Pageable pageable) {
        getProject(projectId);
        return teamRepository.findByProjectIdAndActiveTrue(projectId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public TeamResponse update(Long projectId, Long teamId, TeamRequest request) {
        Team team = teamRepository.findByIdAndProjectId(teamId, projectId).filter(Team::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        String name = normalize(request.getName());
        if (!team.getName().equalsIgnoreCase(name)
                && teamRepository.existsByProjectIdAndNameIgnoreCaseAndActiveTrue(projectId, name)) {
            throw new ConflictException("An active team with this name already exists in the project: " + name);
        }
        team.setName(name);
        team.setDescription(request.getDescription());
        return toResponse(teamRepository.save(team));
    }

    @Override
    @Transactional
    public void delete(Long projectId, Long teamId) {
        Team team = teamRepository.findByIdAndProjectId(teamId, projectId).filter(Team::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        team.setActive(false);
    }

    private Project getProject(Long projectId) {
        return projectRepository.findByIdAndActiveTrue(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().replaceAll("\\s+", " ");
    }

    private TeamResponse toResponse(Team team) {
        return TeamResponse.builder().id(team.getId()).name(team.getName()).description(team.getDescription())
                .active(team.getActive()).projectId(team.getProject().getId()).projectName(team.getProject().getName())
                .memberCount(team.getMembers().size()).createdAt(team.getCreatedAt()).updatedAt(team.getUpdatedAt()).build();
    }
}
