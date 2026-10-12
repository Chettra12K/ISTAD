package com.chettra.devflow.service;

import com.chettra.devflow.dto.request.ProjectPageRequest;
import com.chettra.devflow.entity.Project;
import com.chettra.devflow.repository.ProjectRepository;
import com.chettra.devflow.specification.ProjectSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectQueryService {
    private final ProjectRepository projectRepository;
    public ProjectQueryService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
    @Transactional(readOnly = true)
    public Page<Project> search(ProjectPageRequest request){
        Sort.Direction direction = Sort.Direction.valueOf(request.directionOrDefault().toUpperCase());
        PageRequest pageable = PageRequest.of(request.pageOrDefault(),request.sizeOrDefault(), Sort.by(direction, request.sortByOrDefault()));
        Specification<Project> specification = Specification
                .where(ProjectSpecification.isActive())
                .and(ProjectSpecification.hasStatus(request.status()))
                .and(ProjectSpecification.hasOwnerId(request.ownerId()));
        return projectRepository.findAll(specification, pageable);
    }
}
