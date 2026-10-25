package com.chettra.devflow.service;

import com.chettra.devflow.dto.projectmember.ProjectMemberRequest;
import com.chettra.devflow.dto.projectmember.ProjectMemberResponse;
import com.chettra.devflow.dto.projectmember.ProjectMemberRoleRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectMemberService {

    ProjectMemberResponse add(Long projectId, ProjectMemberRequest request);

    ProjectMemberResponse getById(Long projectId, Long memberId);

    Page<ProjectMemberResponse> getAll(Long projectId, Pageable pageable);

    ProjectMemberResponse updateRole(Long projectId, Long memberId, ProjectMemberRoleRequest request);

    void remove(Long projectId, Long memberId);

    ProjectMemberResponse addToTeam(Long projectId, Long teamId, Long memberId);

    ProjectMemberResponse removeFromTeam(Long projectId, Long teamId, Long memberId);
}
