package com.chettra.devflow.controller;

import com.chettra.devflow.dto.project.ProjectRequest;
import com.chettra.devflow.dto.project.ProjectResponse;
import com.chettra.devflow.service.ProjectService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {
	
	private final ProjectService projectService;
	
	@PostMapping
	public ResponseEntity<ProjectResponse> create(
			@Valid @RequestBody ProjectRequest request
	) {
		
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(projectService.create(request));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProjectResponse> getById(
			@PathVariable Long id
	) {
		
		return ResponseEntity.ok(
				projectService.getById(id)
		);
	}
	
	@GetMapping
	public ResponseEntity<Page<ProjectResponse>> getAll(
			@RequestParam(defaultValue = "0")
			@Min(value = 0, message = "Page must be >= 0")
			int page,
			
			@RequestParam(defaultValue = "10")
			@Min(value = 1, message = "Size must be >= 1")
			int size,
			
			@RequestParam(defaultValue = "createdAt")
			String sortBy,
			
			@RequestParam(defaultValue = "desc")
			String direction
	) {
		
		Sort.Direction sortDirection =
				Sort.Direction.fromString(direction);
		
		Pageable pageable = PageRequest.of(
				page,
				size,
				Sort.by(sortDirection, sortBy)
		);
		
		return ResponseEntity.ok(
				projectService.getAll(pageable)
		);
	}
	
	@GetMapping("/search")
	public ResponseEntity<Page<ProjectResponse>> search(
			@RequestParam String keyword,
			
			@RequestParam(defaultValue = "0")
			@Min(0)
			int page,
			
			@RequestParam(defaultValue = "10")
			@Min(1)
			int size
	) {
		
		Pageable pageable = PageRequest.of(
				page,
				size,
				Sort.by(
						Sort.Direction.DESC,
						"createdAt"
				)
		);
		
		return ResponseEntity.ok(
				projectService.search(keyword, pageable)
		);
	}
	
	@GetMapping("/owner/{ownerId}")
	public ResponseEntity<Page<ProjectResponse>> getByOwner(
			@PathVariable Long ownerId,
			
			@RequestParam(defaultValue = "0")
			@Min(0)
			int page,
			
			@RequestParam(defaultValue = "10")
			@Min(1)
			int size
	) {
		
		Pageable pageable = PageRequest.of(
				page,
				size,
				Sort.by(
						Sort.Direction.DESC,
						"createdAt"
				)
		);
		
		return ResponseEntity.ok(
				projectService.getByOwner(
						ownerId,
						pageable
				)
		);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ProjectResponse> update(
			@PathVariable Long id,
			@Valid @RequestBody ProjectRequest request
	) {
		
		return ResponseEntity.ok(
				projectService.update(id, request)
		);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void archive(
			@PathVariable Long id
	) {
		
		projectService.archive(id);
	}
}