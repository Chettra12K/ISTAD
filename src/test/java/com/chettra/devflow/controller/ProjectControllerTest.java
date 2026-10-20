package com.chettra.devflow.controller;

import com.chettra.devflow.common.exception.ConflictException;
import com.chettra.devflow.common.exception.ResourceNotFoundException;
import com.chettra.devflow.dto.project.ProjectRequest;
import com.chettra.devflow.dto.project.ProjectResponse;
import com.chettra.devflow.dto.request.ProjectPageRequest;
import com.chettra.devflow.entity.ProjectStatus;
import com.chettra.devflow.service.ProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.web.OAuth2ResourceServerWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@ImportAutoConfiguration(
		exclude = OAuth2ResourceServerWebSecurityAutoConfiguration.class
)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

	@Autowired
	private MockMvc mockMvc;

//	@Autowired
//	private ObjectMapper objectMapper;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@MockitoBean
	private ProjectService projectService;

	@Test
	void create_shouldReturn201() throws Exception {

		ProjectRequest request = new ProjectRequest(
				"DevFlow API",
				"Project management API",
				1L,
				ProjectStatus.PLANNING
		);

		ProjectResponse response = ProjectResponse.builder()
				.id(1L)
				.name("DevFlow API")
				.build();

		when(projectService.create(any(ProjectRequest.class)))
				.thenReturn(response);

		mockMvc.perform(
						post("/api/v1/projects")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request))
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("DevFlow API"));

		verify(projectService)
				.create(any(ProjectRequest.class));
	}

	@Test
	void getById_shouldReturn200() throws Exception {

		ProjectResponse response = ProjectResponse.builder()
				.id(1L)
				.name("DevFlow API")
				.build();

		when(projectService.getById(1L))
				.thenReturn(response);

		mockMvc.perform(
						get("/api/v1/projects/1")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("DevFlow API"));

		verify(projectService)
				.getById(1L);
	}

	@Test
	void update_shouldReturn200() throws Exception {

		ProjectRequest request = new ProjectRequest(
				"DevFlow Updated",
				"Updated project",
				1L,
				ProjectStatus.ACTIVE
		);

		ProjectResponse response = ProjectResponse.builder()
				.id(1L)
				.name("DevFlow Updated")
				.build();

		when(projectService.update(
				eq(1L),
				any(ProjectRequest.class)
		)).thenReturn(response);

		mockMvc.perform(
						put("/api/v1/projects/1")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request))
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name")
						.value("DevFlow Updated"));

		verify(projectService)
				.update(eq(1L), any(ProjectRequest.class));
	}

	@Test
	void archive_shouldReturn204() throws Exception {

		doNothing()
				.when(projectService)
				.archive(1L);

		mockMvc.perform(
						delete("/api/v1/projects/1")
				)
				.andExpect(status().isNoContent());

		verify(projectService)
				.archive(1L);
	}

	@Test
	void getAll_shouldReturn200() throws Exception {

		ProjectResponse response = ProjectResponse.builder()
				.id(1L)
				.name("DevFlow API")
				.build();

		PageImpl<ProjectResponse> page =
				new PageImpl<>(
						List.of(response),
						PageRequest.of(0, 10),
						1
				);

		when(projectService.getAll(any()))
				.thenReturn(page);

		mockMvc.perform(
						get("/api/v1/projects")
								.param("page", "0")
								.param("size", "10")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id")
						.value(1))
				.andExpect(jsonPath("$.content[0].name")
						.value("DevFlow API"));

		verify(projectService)
				.getAll(any());
	}

	@Test
	void create_shouldReturn400WhenNameIsBlank() throws Exception {

		ProjectRequest request = new ProjectRequest(
				"",
				"Project description",
				1L,
				ProjectStatus.PLANNING
		);

		mockMvc.perform(
						post("/api/v1/projects")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request))
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message")
						.value("Validation failed"))
				.andExpect(jsonPath("$.validationErrors.name")
						.exists());

		verifyNoInteractions(projectService);
	}

	@Test
	void create_shouldReturn400WhenOwnerIdIsMissing() throws Exception {

		ProjectRequest request = new ProjectRequest(
				"DevFlow API",
				"Project description",
				null,
				ProjectStatus.PLANNING
		);

		mockMvc.perform(
						post("/api/v1/projects")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request))
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.validationErrors.ownerId")
						.exists());

		verifyNoInteractions(projectService);
	}

	@Test
	void create_shouldReturn400WhenStatusIsMissing() throws Exception {

		ProjectRequest request = new ProjectRequest(
				"DevFlow API",
				"Project description",
				1L,
				null
		);

		mockMvc.perform(
						post("/api/v1/projects")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request))
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.validationErrors.status")
						.exists());

		verifyNoInteractions(projectService);
	}

	@Test
	void getAll_shouldReturn400WhenPageIsNegative() throws Exception {

		mockMvc.perform(
						get("/api/v1/projects")
								.param("page", "-1")
								.param("size", "10")
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message")
						.value("Validation failed"))
				.andExpect(jsonPath("$.validationErrors")
						.isNotEmpty());

		verifyNoInteractions(projectService);
	}

	@Test
	void getAll_shouldReturn400WhenSizeIsZero() throws Exception {

		mockMvc.perform(
						get("/api/v1/projects")
								.param("page", "0")
								.param("size", "0")
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.validationErrors")
						.isNotEmpty());

		verifyNoInteractions(projectService);
	}

	@Test
	void getAll_shouldReturn400WhenSortFieldIsInvalid() throws Exception {

		mockMvc.perform(
						get("/api/v1/projects")
								.param("page", "0")
								.param("size", "10")
								.param("sortBy", "password")
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message")
						.value("Invalid sort field: password"));

		verifyNoInteractions(projectService);
	}

	@Test
	void getAll_shouldReturn400WhenSortDirectionIsInvalid() throws Exception {

		mockMvc.perform(
						get("/api/v1/projects")
								.param("page", "0")
								.param("size", "10")
								.param("sortBy", "createdAt")
								.param("direction", "random")
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message")
						.value(
								"Invalid sort direction: random. Allowed values: asc, desc"
						));

		verifyNoInteractions(projectService);
	}

	@Test
	void getById_shouldReturn404WhenProjectDoesNotExist() throws Exception {

		when(projectService.getById(999L))
				.thenThrow(
						new ResourceNotFoundException(
								"Project not found with id: 999"
						)
				);

		mockMvc.perform(
						get("/api/v1/projects/999")
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message")
						.value("Project not found with id: 999"));

		verify(projectService)
				.getById(999L);
	}

	@Test
	void create_shouldReturn409WhenProjectAlreadyExists() throws Exception {

		ProjectRequest request = new ProjectRequest(
				"DevFlow API",
				"Project description",
				1L,
				ProjectStatus.PLANNING
		);

		when(projectService.create(any(ProjectRequest.class)))
				.thenThrow(
						new ConflictException(
								"An active project with this name already exists for this owner: DevFlow API"
						)
				);

		mockMvc.perform(
						post("/api/v1/projects")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request))
				)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.message")
						.value(
								"An active project with this name already exists for this owner: DevFlow API"
						));

		verify(projectService)
				.create(any(ProjectRequest.class));
	}

	@Test
	void search_shouldReturn200() throws Exception {

		ProjectResponse response = ProjectResponse.builder()
				.id(1L)
				.name("DevFlow API")
				.build();

		PageImpl<ProjectResponse> page =
				new PageImpl<>(
						List.of(response),
						PageRequest.of(0, 10),
						1
				);

		when(projectService.search(any(ProjectPageRequest.class)))
				.thenReturn(page);

		mockMvc.perform(
						get("/api/v1/projects/search")
								.param("page", "0")
								.param("size", "10")
								.param("sortBy", "createdAt")
								.param("direction", "DESC")
								.param("status", "PLANNING")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id")
						.value(1))
				.andExpect(jsonPath("$.content[0].name")
						.value("DevFlow API"));

		verify(projectService)
				.search(any(ProjectPageRequest.class));
	}

	@Test
	void search_shouldSupportOwnerFilter() throws Exception {

		ProjectResponse response = ProjectResponse.builder()
				.id(1L)
				.name("DevFlow API")
				.build();

		PageImpl<ProjectResponse> page =
				new PageImpl<>(
						List.of(response),
						PageRequest.of(0, 10),
						1
				);

		when(projectService.search(any(ProjectPageRequest.class)))
				.thenReturn(page);

		mockMvc.perform(
						get("/api/v1/projects/search")
								.param("page", "0")
								.param("size", "10")
								.param("ownerId", "1")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id")
						.value(1))
				.andExpect(jsonPath("$.content[0].name")
						.value("DevFlow API"));

		verify(projectService)
				.search(any(ProjectPageRequest.class));
	}

	@Test
	void search_shouldReturn400WhenPageIsNegative() throws Exception {

		mockMvc.perform(
						get("/api/v1/projects/search")
								.param("page", "-1")
								.param("size", "10")
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message")
						.value("Validation failed"))
				.andExpect(jsonPath("$.validationErrors")
						.isNotEmpty());

		verifyNoInteractions(projectService);
	}

	@Test
	void search_shouldReturn400WhenSizeExceedsMaximum() throws Exception {

		mockMvc.perform(
						get("/api/v1/projects/search")
								.param("page", "0")
								.param("size", "101")
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.validationErrors")
						.isNotEmpty());

		verifyNoInteractions(projectService);
	}

	@Test
	void search_shouldReturn400WhenSortFieldIsInvalid() throws Exception {

		mockMvc.perform(
						get("/api/v1/projects/search")
								.param("sortBy", "password")
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.validationErrors.sortBy")
						.exists());

		verifyNoInteractions(projectService);
	}

	@Test
	void search_shouldReturn400WhenDirectionIsInvalid() throws Exception {

		mockMvc.perform(
						get("/api/v1/projects/search")
								.param("direction", "INVALID")
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.validationErrors.direction")
						.exists());

		verifyNoInteractions(projectService);
	}

	@Test
	void getByOwner_shouldReturn200() throws Exception {

		ProjectResponse response = ProjectResponse.builder()
				.id(1L)
				.name("DevFlow API")
				.build();

		PageImpl<ProjectResponse> page =
				new PageImpl<>(
						List.of(response),
						PageRequest.of(0, 10),
						1
				);

		when(projectService.getByOwner(
				eq(1L),
				any()
		)).thenReturn(page);

		mockMvc.perform(
						get("/api/v1/projects/owner/1")
								.param("page", "0")
								.param("size", "10")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id")
						.value(1))
				.andExpect(jsonPath("$.content[0].name")
						.value("DevFlow API"));

		verify(projectService)
				.getByOwner(eq(1L), any());
	}

	@Test
	void getByOwner_shouldReturn400WhenPageIsNegative() throws Exception {

		mockMvc.perform(
						get("/api/v1/projects/owner/1")
								.param("page", "-1")
								.param("size", "10")
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.validationErrors")
						.isNotEmpty());

		verifyNoInteractions(projectService);
	}

	@Test
	void getByOwner_shouldReturn400WhenSizeIsZero() throws Exception {

		mockMvc.perform(
						get("/api/v1/projects/owner/1")
								.param("page", "0")
								.param("size", "0")
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.status").value(400));

		verifyNoInteractions(projectService);
	}
}