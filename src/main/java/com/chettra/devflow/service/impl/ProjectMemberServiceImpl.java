package com.chettra.devflow.service.impl;

import com.chettra.devflow.common.exception.BadRequestException;
import com.chettra.devflow.common.exception.ConflictException;
import com.chettra.devflow.common.exception.ResourceNotFoundException;
import com.chettra.devflow.dto.projectmember.ProjectMemberRequest;
import com.chettra.devflow.dto.projectmember.ProjectMemberResponse;
import com.chettra.devflow.dto.projectmember.ProjectMemberRoleRequest;
import com.chettra.devflow.entity.Project;
import com.chettra.devflow.entity.ProjectMember;
import com.chettra.devflow.entity.User;
import com.chettra.devflow.entity.Team;
import com.chettra.devflow.repository.ProjectMemberRepository;
import com.chettra.devflow.repository.ProjectRepository;
import com.chettra.devflow.repository.TeamRepository;
import com.chettra.devflow.repository.UserRepository;
import com.chettra.devflow.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Override
    public ProjectMemberResponse add(Long projectId, ProjectMemberRequest request) {
        Project project = getProject(projectId);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new BadRequestException("Cannot add an inactive user to a project");
        }

        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, request.getUserId())
                .orElse(null);

        if (member != null) {
            if (Boolean.TRUE.equals(member.getActive())) {
                throw new ConflictException("User is already a member of this project");
            }
            member.setActive(true);
            member.setRole(request.getRole());
            return toResponse(projectMemberRepository.save(member));
        }

        member = ProjectMember.builder()
                .project(project)
                .user(user)
                .role(request.getRole())
                .active(true)
                .build();

        return toResponse(projectMemberRepository.save(member));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectMemberResponse getById(Long projectId, Long memberId) {
        return toResponse(getActiveMember(projectId, memberId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectMemberResponse> getAll(Long projectId, Pageable pageable) {
        getProject(projectId);
        return projectMemberRepository.findByProjectIdAndActiveTrue(projectId, pageable)
                .map(this::toResponse);
    }

    @Override
    public ProjectMemberResponse updateRole(Long projectId, Long memberId, ProjectMemberRoleRequest request) {
        ProjectMember member = getActiveMember(projectId, memberId);
        member.setRole(request.getRole());
        return toResponse(projectMemberRepository.save(member));
    }

    @Override
    public void remove(Long projectId, Long memberId) {
        ProjectMember member = getActiveMember(projectId, memberId);
        member.setActive(false);
        member.getTeams().clear();
        projectMemberRepository.save(member);
    }

    @Override
    public ProjectMemberResponse addToTeam(Long projectId, Long teamId, Long memberId) {
        ProjectMember member = getActiveMember(projectId, memberId);
        Team team = teamRepository.findByIdAndProjectId(teamId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));

        if (!Boolean.TRUE.equals(team.getActive())) {
            throw new BadRequestException("Cannot add a member to an inactive team");
        }

        team.getMembers().add(member);
        return toResponse(member);
    }

    @Override
    public ProjectMemberResponse removeFromTeam(Long projectId, Long teamId, Long memberId) {
        ProjectMember member = getActiveMember(projectId, memberId);
        Team team = teamRepository.findByIdAndProjectId(teamId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));

        team.getMembers().remove(member);
        return toResponse(member);
    }

    private Project getProject(Long projectId) {
        return projectRepository.findByIdAndActiveTrue(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
    }

    private ProjectMember getActiveMember(Long projectId, Long memberId) {
        getProject(projectId);
        ProjectMember member = projectMemberRepository.findByIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project member not found: " + memberId));
        if (!Boolean.TRUE.equals(member.getActive())) {
            throw new ResourceNotFoundException("Project member not found: " + memberId);
        }
        return member;
    }

    private ProjectMemberResponse toResponse(ProjectMember member) {
        User user = member.getUser();
        return ProjectMemberResponse.builder()
                .id(member.getId())
                .projectId(member.getProject().getId())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(member.getRole())
                .active(member.getActive())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
