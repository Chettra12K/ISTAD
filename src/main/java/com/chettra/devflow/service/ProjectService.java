package com.chettra.devflow.service;

import com.chettra.devflow.dto.project.ProjectRequest;
import com.chettra.devflow.dto.project.ProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
	
	ProjectResponse create(ProjectRequest request);
	ProjectResponse getById(Long id);
	ProjectResponse update(Long id, ProjectRequest request);
	Page<ProjectResponse> getAll(Pageable pageable);
	Page<ProjectResponse> search(String keyword, Pageable pageable);
	Page<ProjectResponse> getByOwner(Long ownerId, Pageable pageable);
	void archive(Long id);
	
}
