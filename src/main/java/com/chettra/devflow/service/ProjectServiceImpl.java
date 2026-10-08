package com.chettra.devflow.service;

import com.chettra.devflow.common.exception.ResourceNotFoundException;
import com.chettra.devflow.dto.project.ProjectRequest;
import com.chettra.devflow.dto.project.ProjectResponse;
import com.chettra.devflow.entity.Project;
import com.chettra.devflow.entity.User;
import com.chettra.devflow.mapper.ProjectMapper;
import com.chettra.devflow.repository.ProjectRepository;
import com.chettra.devflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
		
		private final ProjectRepository projectRepository;
		private final UserRepository userRepository;
		private final ProjectMapper projectMapper;
		
		@Override
		public ProjectResponse create(ProjectRequest request) {
			
			User owner = userRepository.findById(request.getOwnerId())
					.orElseThrow(() ->
							new ResourceNotFoundException(
									"Owner not found with id: "
											+ request.getOwnerId()
							)
					);
			
			if (projectRepository.existsByNameIgnoreCaseAndOwnerId(
					request.getName(),
					request.getOwnerId()
			)) {
				throw new IllegalArgumentException(
						"Project already exists for this owner"
				);
			}
			
			Project project = Project.builder()
					.name(request.getName().trim())
					.description(request.getDescription())
					.owner(owner)
					.active(true)
					.build();
			
			return projectMapper.toResponse(
					projectRepository.save(project)
			);
		}
		
		@Override
		//@Transactional(readOnly = true)
		public ProjectResponse getById(Long id) {
			
			Project project = projectRepository
					.findByIdAndActiveTrue(id)
					.orElseThrow(() ->
							new ResourceNotFoundException(
									"Project not found with id: " + id
							)
					);
			
			return projectMapper.toResponse(project);
		}
		
		@Override
		//@Transactional(readOnly = true)
		public Page<ProjectResponse> getAll(Pageable pageable) {
			
			return projectRepository
					.findByActiveTrue(pageable)
					.map(projectMapper::toResponse);
		}
		
		@Override
		//@Transactional(readOnly = true)
		public Page<ProjectResponse> search(
				String keyword,
				Pageable pageable
		) {
			
			return projectRepository
					.findByNameContainingIgnoreCaseAndActiveTrue(
							keyword,
							pageable
					)
					.map(projectMapper::toResponse);
		}
		
		@Override
		//@Transactional(readOnly = true)
		public Page<ProjectResponse> getByOwner(
				Long ownerId,
				Pageable pageable
		) {
			
			if (!userRepository.existsById(ownerId)) {
				throw new ResourceNotFoundException(
						"Owner not found with id: " + ownerId
				);
			}
			
			return projectRepository
					.findByOwnerIdAndActiveTrue(
							ownerId,
							pageable
					)
					.map(projectMapper::toResponse);
		}
		
		@Override
		public ProjectResponse update(
				Long id,
				ProjectRequest request
		) {
			
			Project project = projectRepository
					.findByIdAndActiveTrue(id)
					.orElseThrow(() ->
							new ResourceNotFoundException(
									"Project not found with id: " + id
							)
					);
			
			User owner = userRepository.findById(request.getOwnerId())
					.orElseThrow(() ->
							new ResourceNotFoundException(
									"Owner not found with id: "
											+ request.getOwnerId()
							)
					);
			
			project.setName(request.getName().trim());
			project.setDescription(request.getDescription());
			project.setOwner(owner);
			
			return projectMapper.toResponse(
					projectRepository.save(project)
			);
		}
		
		@Override
		public void archive(Long id) {
			
			Project project = projectRepository
					.findByIdAndActiveTrue(id)
					.orElseThrow(() ->
							new ResourceNotFoundException(
									"Project not found with id: " + id
							)
					);
			
			project.setActive(false);
			
			projectRepository.save(project);
		}
}
