package com.chettra.devflow.service;

import com.chettra.devflow.common.exception.BadRequestException;
import com.chettra.devflow.common.exception.ResourceNotFoundException;
import com.chettra.devflow.common.util.ProjectNameNormalizer;
import com.chettra.devflow.dto.project.ProjectRequest;
import com.chettra.devflow.dto.project.ProjectResponse;
import com.chettra.devflow.dto.request.ProjectPageRequest;
import com.chettra.devflow.entity.Project;
import com.chettra.devflow.entity.ProjectStatus;
import com.chettra.devflow.entity.User;
import com.chettra.devflow.mapper.ProjectMapper;
import com.chettra.devflow.repository.ProjectRepository;
import com.chettra.devflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {
		
		private final ProjectRepository projectRepository;
		private final UserRepository userRepository;
		private final ProjectMapper projectMapper;
		private final ProjectQueryService projectQueryService;
		
		@Override
		@Transactional
		public ProjectResponse create(ProjectRequest request) {
			
			if (request.getStatus() == ProjectStatus.ARCHIVED) {
				throw new  BadRequestException("Project cannot be created with ARCHIVED status");
			}
			String projectName = ProjectNameNormalizer.normalize(request.getName());
			User owner = userRepository.findById(request.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
			if (projectRepository.existsByNameIgnoreCaseAndOwnerId(projectName, request.getOwnerId())) {
				throw new BadRequestException("Project already exists for this owner" + projectName);
			}
			Project project = Project.builder()
					.name(projectName)
					.description(request.getDescription())
					.owner(owner)
					.status(request.getStatus())
					.active(true)
					.build();
			return projectMapper.toResponse(projectRepository.save(project));
		}
		
		@Override
		@Transactional(readOnly = true)
		public ProjectResponse getById(Long id) {
			Project project = projectRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
			return projectMapper.toResponse(project);
		}
		
		@Override
		@Transactional(readOnly = true)
		public Page<ProjectResponse> getAll(Pageable pageable) {
			return projectRepository.findByActiveTrue(pageable).map(projectMapper::toResponse);
		}

		@Override
		@Transactional(readOnly = true)
		public Page<ProjectResponse> search(ProjectPageRequest request) {

			Page<Project> projects = projectQueryService.search(request);
			return projects.map(projectMapper::toResponse);
		}
		
		@Override
		@Transactional
		public Page<ProjectResponse> getByOwner(Long ownerId, Pageable pageable) {
			if (!userRepository.existsById(ownerId)) {
				throw new ResourceNotFoundException(
						"Owner not found with id: " + ownerId
				);
			}
			return projectRepository.findByOwnerIdAndActiveTrue(ownerId, pageable).map(projectMapper::toResponse);
		}
		
		@Override
		public ProjectResponse update(Long id, ProjectRequest request) {
			
			if (request.getStatus() == ProjectStatus.ARCHIVED) {
				throw new  BadRequestException("Project cannot be updated with ARCHIVED status. Use DELETE to archive the project");
			}
			Project project = projectRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
			User owner = userRepository.findById(request.getOwnerId()).orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
			String projectName = ProjectNameNormalizer.normalize(request.getName());
			if (!project.getName().equalsIgnoreCase(projectName)
					&& projectRepository.existsByNameIgnoreCaseAndOwnerId(
					projectName,
					request.getOwnerId()
			)) {
				throw new BadRequestException(
						"Project already exists for this owner: " + projectName
				);
			}
			project.setName(projectName);
			project.setDescription(request.getDescription());
			project.setOwner(owner);
			project.setStatus(request.getStatus());
			return projectMapper.toResponse(projectRepository.save(project));
		}
		
		@Override
		@Transactional
		public void archive(Long id) {

			Project project = projectRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
			project.setActive(false);
			projectRepository.save(project);
		}
}
