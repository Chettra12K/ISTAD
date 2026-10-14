
package com.chettra.devflow.repository;

import com.chettra.devflow.entity.Project;
import com.chettra.devflow.entity.ProjectStatus;
import com.chettra.devflow.entity.User;
import com.chettra.devflow.repository.ProjectRepository;
import com.chettra.devflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
class ProjectRepositoryIntegrationTest {
	
	@Container
	static final PostgreSQLContainer<?> POSTGRES =
			new PostgreSQLContainer<>("postgres:16-alpine")
					.withDatabaseName("devflow_test")
					.withUsername("devflow_test")
					.withPassword("devflow_test");
	
	@DynamicPropertySource
	static void configureDatabase(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
		registry.add("spring.datasource.username", POSTGRES::getUsername);
		registry.add("spring.datasource.password", POSTGRES::getPassword);
		registry.add("spring.datasource.driver-class-name",
				POSTGRES::getDriverClassName);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
	}
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private User owner;
	
	@BeforeEach
	void setUp() {
		projectRepository.deleteAll();
		userRepository.deleteAll();
		
		// The index currently exists as a manually created database index.
		// Create it here so the disposable test database has the same rule.
		jdbcTemplate.execute("""
                CREATE UNIQUE INDEX IF NOT EXISTS uk_project_owner_lower_name
                ON projects (owner_id, LOWER(name))
                WHERE active = true
                """);
		
		owner = userRepository.saveAndFlush(
				User.builder()
						.username("integration-owner")
						.email("integration-owner@devflow.test")
						.password("test-password")
						.active(true)
						.build()
		);
	}
	
	@Test
	void save_shouldPersistProjectWithOwner() {
		Project project = projectRepository.saveAndFlush(
				buildProject("DevFlow API", true)
		);
		
		assertNotNull(project.getId());
		assertEquals("DevFlow API", project.getName());
		assertEquals(owner.getId(), project.getOwner().getId());
	}
	
	@Test
	void save_shouldRejectCaseInsensitiveDuplicateForSameOwner() {
		projectRepository.saveAndFlush(buildProject("DevFlow API", true));
		
		assertThrows(
				DataIntegrityViolationException.class,
				() -> projectRepository.saveAndFlush(
						buildProject("devflow api", true)
				)
		);
	}
	
	@Test
	void save_shouldAllowSameNameWhenPreviousProjectIsArchived() {
		projectRepository.saveAndFlush(buildProject("DevFlow API", false));
		
		Project activeProject =
				projectRepository.saveAndFlush(
						buildProject("devflow api", true)
				);
		
		assertNotNull(activeProject.getId());
	}
	
	private Project buildProject(String name, boolean active) {
		return Project.builder()
				.name(name)
				.description("PostgreSQL integration test")
				.owner(owner)
				.status(ProjectStatus.PLANNING)
				.active(active)
				.build();
	}
}
