package com.chettra.devflow.service;

import com.chettra.devflow.dto.project.ProjectRequest;
import com.chettra.devflow.dto.project.ProjectResponse;
import com.chettra.devflow.dto.request.ProjectPageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
	
	ProjectResponse create(ProjectRequest request);
	ProjectResponse getById(Long id);
	ProjectResponse update(Long id, ProjectRequest request);
	Page<ProjectResponse> getAll(Pageable pageable, Long currentUserId, boolean administrator);
	Page<ProjectResponse> search(ProjectPageRequest request, Long currentUserId, boolean administrator);
	Page<ProjectResponse> getByOwner(Long ownerId, Pageable pageable);
	void archive(Long id);
	
}
