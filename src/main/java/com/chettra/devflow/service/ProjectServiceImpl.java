package com.chettra.devflow.service;

import com.chettra.devflow.common.exception.BadRequestException;
import com.chettra.devflow.common.exception.ConflictException;
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
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
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

	// =========================================================
	// CREATE
	// =========================================================

	@Override
	@Transactional
	public ProjectResponse create(ProjectRequest request) {

		if (request.getStatus() == ProjectStatus.ARCHIVED) {
			throw new BadRequestException(
					"Project cannot be created with ARCHIVED status"
			);
		}

		String projectName =
				ProjectNameNormalizer.normalize(request.getName());

		User owner = userRepository.findById(request.getOwnerId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Owner not found with id: "
								+ request.getOwnerId()
				));

		boolean duplicateExists =
				projectRepository.existsByNameIgnoreCaseAndOwnerIdAndActiveTrue(
						projectName,
						request.getOwnerId()
				);

		if (duplicateExists) {
			throw new ConflictException(
					"An active project with this name already exists "
							+ "for this owner: "
							+ projectName
			);
		}

		Project project = Project.builder()
				.name(projectName)
				.description(request.getDescription())
				.owner(owner)
				.status(request.getStatus())
				.active(true)
				.build();

		Project savedProject = saveProjectOrThrowConflict(project, projectName);

		return projectMapper.toResponse(savedProject);
	}

	// =========================================================
	// GET BY ID
	// =========================================================

	@Override
	public ProjectResponse getById(Long id) {

		Project project = projectRepository
				.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Project not found with id: " + id
				));

		return projectMapper.toResponse(project);
	}

	// =========================================================
	// GET ALL
	// =========================================================

	@Override
	public Page<ProjectResponse> getAll(Pageable pageable) {

		return projectRepository
				.findByActiveTrue(pageable)
				.map(projectMapper::toResponse);
	}

	// =========================================================
	// SEARCH
	// =========================================================

	@Override
	public Page<ProjectResponse> search(ProjectPageRequest request) {

		Page<Project> projects =
				projectQueryService.search(request);

		return projects.map(projectMapper::toResponse);
	}

	// =========================================================
	// GET BY OWNER
	// =========================================================

	@Override
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
				.findByOwnerIdAndActiveTrue(ownerId, pageable)
				.map(projectMapper::toResponse);
	}

	// =========================================================
	// UPDATE
	// =========================================================

	@Override
	@Transactional
	public ProjectResponse update(
			Long id,
			ProjectRequest request
	) {

		if (request.getStatus() == ProjectStatus.ARCHIVED) {
			throw new BadRequestException(
					"Project cannot be updated with ARCHIVED status. "
							+ "Use DELETE to archive the project"
			);
		}

		Project project = projectRepository
				.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Project not found with id: " + id
				));

		User owner = userRepository
				.findById(request.getOwnerId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Owner not found with id: "
								+ request.getOwnerId()
				));

		String projectName =
				ProjectNameNormalizer.normalize(request.getName());

		boolean nameChanged =
				!project.getName().equalsIgnoreCase(projectName);

		boolean ownerChanged =
				!project.getOwner().getId().equals(owner.getId());

		/*
		 * Check duplicate only when either the project name
		 * or the owner has changed.
		 *
		 * This prevents the project from detecting itself
		 * as a duplicate during a normal update.
		 */
		if ((nameChanged || ownerChanged)
				&& projectRepository
				.existsByNameIgnoreCaseAndOwnerIdAndActiveTrue(
						projectName,
						owner.getId()
				)) {

			throw new ConflictException(
					"An active project with this name already exists "
							+ "for this owner: "
							+ projectName
			);
		}

		project.setName(projectName);
		project.setDescription(request.getDescription());
		project.setOwner(owner);
		project.setStatus(request.getStatus());

		Project updatedProject =
				saveProjectOrThrowConflict(project, projectName);

		return projectMapper.toResponse(updatedProject);
	}

	// =========================================================
	// ARCHIVE
	// =========================================================

	@Override
	@Transactional
	public void archive(Long id) {

		Project project = projectRepository
				.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Project not found with id: " + id
				));

		project.setActive(false);

		projectRepository.save(project);
	}

	private Project saveProjectOrThrowConflict(
			Project project,
			String projectName
	) {
		try {
			// Flush immediately so the database uniqueness check happens here.
			return projectRepository.saveAndFlush(project);

		} catch (DataIntegrityViolationException exception) {

			if (isProjectNameUniqueViolation(exception)) {
				throw new ConflictException(
						"An active project with this name already exists "
								+ "for this owner: "
								+ projectName
				);
			}

			// Do not hide unrelated database integrity errors.
			throw exception;
		}
	}

	private boolean isProjectNameUniqueViolation(
			DataIntegrityViolationException exception
	) {
		Throwable cause = exception;

		while (cause != null) {

			if (cause instanceof ConstraintViolationException violation) {
				return "uk_project_owner_lower_name"
						.equalsIgnoreCase(violation.getConstraintName());
			}

			cause = cause.getCause();
		}

		return false;
	}
}