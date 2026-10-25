package com.chettra.devflow.service;

import com.chettra.devflow.dto.team.TeamRequest;
import com.chettra.devflow.dto.team.TeamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamService {
    TeamResponse create(Long projectId, TeamRequest request);
    TeamResponse getById(Long projectId, Long teamId);
    Page<TeamResponse> getAll(Long projectId, Pageable pageable);
    TeamResponse update(Long projectId, Long teamId, TeamRequest request);
    void delete(Long projectId, Long teamId);
}
