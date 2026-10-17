//package com.chettra.devflow.service;
//
//import com.chettra.devflow.common.exception.BadRequestException;
//import com.chettra.devflow.common.exception.ConflictException;
//import com.chettra.devflow.common.exception.ResourceNotFoundException;
//import com.chettra.devflow.dto.project.ProjectRequest;
//import com.chettra.devflow.dto.project.ProjectResponse;
//import com.chettra.devflow.entity.Project;
//import com.chettra.devflow.entity.ProjectStatus;
//import com.chettra.devflow.entity.User;
//import com.chettra.devflow.mapper.ProjectMapper;
//import com.chettra.devflow.repository.ProjectRepository;
//import com.chettra.devflow.repository.UserRepository;
//import org.hibernate.exception.ConstraintViolationException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.dao.DataIntegrityViolationException;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProjectServiceImplTest {
//
//	@Mock
//	private ProjectRepository projectRepository;
//
//	@Mock
//	private UserRepository userRepository;
//
//	@Mock
//	private ProjectMapper projectMapper;
//
//	@Mock
//	private ProjectQueryService projectQueryService;
//
//	@InjectMocks
//	private ProjectServiceImpl projectService;
//
//	private User owner;
//	private Project project;
//	private ProjectRequest request;
//
//	@BeforeEach
//	void setUp() {
//
//		owner = User.builder()
//				.id(1L)
//				.username("owner")
//				.email("owner@example.com")
//				.build();
//
//		project = Project.builder()
//				.id(1L)
//				.name("DevFlow")
//				.description("Project management API")
//				.owner(owner)
//				.status(ProjectStatus.PLANNING)
//				.active(true)
//				.build();
//
//		request = new ProjectRequest(
//				"DevFlow",
//				"Project management API",
//				1L,
//				ProjectStatus.PLANNING
//		);
//	}
//
//	@Test
//	void create_shouldCreateProjectSuccessfully() {
//
//		ProjectResponse response = ProjectResponse.builder()
//				.id(1L)
//				.name("DevFlow")
//				.build();
//
//		when(userRepository.findById(1L))
//				.thenReturn(Optional.of(owner));
//
//		when(projectRepository
//				.existsByNameIgnoreCaseAndOwnerIdAndActiveTrue(
//						"DevFlow",
//						1L
//				))
//				.thenReturn(false);
//
//		when(projectRepository.saveAndFlush(any(Project.class)))
//				.thenReturn(project);
//
//		when(projectMapper.toResponse(project))
//				.thenReturn(response);
//
//		ProjectResponse result =
//				projectService.create(request);
//
//		assertEquals(1L, result.getId());
//		assertEquals("DevFlow", result.getName());
//
//		verify(userRepository).findById(1L);
//
//		verify(projectRepository)
//				.existsByNameIgnoreCaseAndOwnerIdAndActiveTrue(
//						"DevFlow",
//						1L
//				);
//
//		verify(projectRepository)
//				.saveAndFlush(any(Project.class));
//
//		verify(projectMapper)
//				.toResponse(project);
//	}
//
//	@Test
//	void create_shouldThrowWhenOwnerNotFound() {
//
//		when(userRepository.findById(1L))
//				.thenReturn(Optional.empty());
//
//		assertThrows(
//				ResourceNotFoundException.class,
//				() -> projectService.create(request)
//		);
//
//		verify(projectRepository, never())
//				.saveAndFlush(any(Project.class));
//	}
//
//	@Test
//	void create_shouldThrowWhenDuplicateProjectExists() {
//
//		when(userRepository.findById(1L))
//				.thenReturn(Optional.of(owner));
//
//		when(projectRepository
//				.existsByNameIgnoreCaseAndOwnerIdAndActiveTrue(
//						"DevFlow",
//						1L
//				))
//				.thenReturn(true);
//
//		assertThrows(
//				ConflictException.class,
//				() -> projectService.create(request)
//		);
//
//		verify(projectRepository, never())
//				.saveAndFlush(any(Project.class));
//	}
//
//	@Test
//	void update_shouldUpdateProjectSuccessfully() {
//
//		ProjectResponse response = ProjectResponse.builder()
//				.id(1L)
//				.name("DevFlow Updated")
//				.build();
//
//		ProjectRequest updateRequest = new ProjectRequest(
//				"DevFlow Updated",
//				"Updated description",
//				1L,
//				ProjectStatus.ACTIVE
//		);
//
//		when(projectRepository.findByIdAndActiveTrue(1L))
//				.thenReturn(Optional.of(project));
//
//		when(userRepository.findById(1L))
//				.thenReturn(Optional.of(owner));
//
//		when(projectRepository
//				.existsByNameIgnoreCaseAndOwnerIdAndActiveTrue(
//						"DevFlow Updated",
//						1L
//				))
//				.thenReturn(false);
//
//		when(projectRepository.saveAndFlush(any(Project.class)))
//				.thenReturn(project);
//
//		when(projectMapper.toResponse(project))
//				.thenReturn(response);
//
//		ProjectResponse result =
//				projectService.update(1L, updateRequest);
//
//		assertEquals(1L, result.getId());
//		assertEquals("DevFlow Updated", result.getName());
//
//		verify(projectRepository)
//				.saveAndFlush(project);
//	}
//
//	@Test
//	void update_shouldThrowWhenProjectNotFound() {
//
//		when(projectRepository.findByIdAndActiveTrue(1L))
//				.thenReturn(Optional.empty());
//
//		assertThrows(
//				ResourceNotFoundException.class,
//				() -> projectService.update(1L, request)
//		);
//
//		verify(projectRepository, never())
//				.saveAndFlush(any(Project.class));
//	}
//
//	@Test
//	void update_shouldThrowWhenDuplicateProjectExists() {
//
//		ProjectRequest updateRequest = new ProjectRequest(
//				"Existing Project",
//				"Updated description",
//				1L,
//				ProjectStatus.PLANNING
//		);
//
//		when(projectRepository.findByIdAndActiveTrue(1L))
//				.thenReturn(Optional.of(project));
//
//		when(userRepository.findById(1L))
//				.thenReturn(Optional.of(owner));
//
//		when(projectRepository
//				.existsByNameIgnoreCaseAndOwnerIdAndActiveTrue(
//						"Existing Project",
//						1L
//				))
//				.thenReturn(true);
//
//		assertThrows(
//				ConflictException.class,
//				() -> projectService.update(1L, updateRequest)
//		);
//
//		verify(projectRepository, never())
//				.saveAndFlush(any(Project.class));
//	}
//
//	@Test
//	void archive_shouldArchiveProjectSuccessfully() {
//
//		when(projectRepository.findByIdAndActiveTrue(1L))
//				.thenReturn(Optional.of(project));
//
//		projectService.archive(1L);
//
//		assertEquals(false, project.getActive());
//
//		verify(projectRepository)
//				.save(project);
//	}
//
//	@Test
//	void archive_shouldThrowWhenProjectNotFound() {
//
//		when(projectRepository.findByIdAndActiveTrue(1L))
//				.thenReturn(Optional.empty());
//
//		assertThrows(
//				ResourceNotFoundException.class,
//				() -> projectService.archive(1L)
//		);
//
//		verify(projectRepository, never())
//				.save(any(Project.class));
//	}
//
//	@Test
//	void create_shouldThrowConflictWhenDatabaseUniqueConstraintIsViolated() {
//
//		ProjectRequest request = new ProjectRequest(
//				"DevFlow API",
//				"Project description",
//				1L,
//				ProjectStatus.PLANNING
//		);
//
//		User owner = User.builder()
//				.id(1L)
//				.username("owner")
//				.email("owner@test.com")
//				.build();
//
//		when(userRepository.findById(1L))
//				.thenReturn(Optional.of(owner));
//
//		// The application performs this check BEFORE saveAndFlush().
//		when(projectRepository.existsByNameIgnoreCaseAndOwnerIdAndActiveTrue(
//				eq("DevFlow API"),
//				eq(1L)
//		)).thenReturn(false);
//
//		ConstraintViolationException constraintViolationException =
//				new ConstraintViolationException(
//						"duplicate project name",
//						null,
//						"uk_project_owner_lower_name"
//				);
//
//		DataIntegrityViolationException databaseException =
//				new DataIntegrityViolationException(
//						"could not execute statement",
//						constraintViolationException
//				);
//
//		when(projectRepository.saveAndFlush(any(Project.class)))
//				.thenThrow(databaseException);
//
//		assertThrows(
//				ConflictException.class,
//				() -> projectService.create(request)
//		);
//
//		verify(projectRepository)
//				.existsByNameIgnoreCaseAndOwnerIdAndActiveTrue(
//						eq("DevFlow API"),
//						eq(1L)
//				);
//
//		verify(projectRepository)
//				.saveAndFlush(any(Project.class));
//	}
//
//	@Test
//	void create_shouldThrowWhenStatusIsArchived() {
//
//		ProjectRequest archivedRequest = new ProjectRequest(
//				"Archived Project",
//				"This project should not be created",
//				1L,
//				ProjectStatus.ARCHIVED
//		);
//
//		assertThrows(
//				BadRequestException.class,
//				() -> projectService.create(archivedRequest)
//		);
//
//		verifyNoInteractions(
//				projectRepository,
//				userRepository,
//				projectMapper,
//				projectQueryService
//		);
//	}
//
//	@Test
//	void update_shouldThrowWhenStatusIsArchived() {
//
//		ProjectRequest archivedRequest = new ProjectRequest(
//				"DevFlow API",
//				"Updated description",
//				1L,
//				ProjectStatus.ARCHIVED
//		);
//
//		assertThrows(
//				BadRequestException.class,
//				() -> projectService.update(1L, archivedRequest)
//		);
//
//		verifyNoInteractions(
//				projectRepository,
//				userRepository,
//				projectMapper
//		);
//	}
//
//	@Test
//	void update_shouldNormalizeProjectNameBeforeSaving() {
//
//		ProjectRequest updateRequest = new ProjectRequest(
//				"   DevFlow    Updated   ",
//				"Updated description",
//				1L,
//				ProjectStatus.PLANNING
//		);
//
//		ProjectResponse response = ProjectResponse.builder()
//				.id(1L)
//				.name("DevFlow Updated")
//				.build();
//
//		when(projectRepository.findByIdAndActiveTrue(1L))
//				.thenReturn(Optional.of(project));
//
//		when(userRepository.findById(1L))
//				.thenReturn(Optional.of(owner));
//
//		when(projectRepository
//				.existsByNameIgnoreCaseAndOwnerIdAndActiveTrue(
//						"DevFlow Updated",
//						1L
//				))
//				.thenReturn(false);
//
//		when(projectRepository.saveAndFlush(any(Project.class)))
//				.thenAnswer(invocation -> invocation.getArgument(0));
//
//		when(projectMapper.toResponse(any(Project.class)))
//				.thenReturn(response);
//
//		ProjectResponse result =
//				projectService.update(1L, updateRequest);
//
//		assertEquals(
//				"DevFlow Updated",
//				result.getName()
//		);
//
//		verify(projectRepository).saveAndFlush(
//				argThat(savedProject ->
//						"DevFlow Updated".equals(savedProject.getName())
//				)
//		);
//	}
//}