
package com.chettra.devflow.controller;

import com.chettra.devflow.dto.role.RoleRequest;
import com.chettra.devflow.dto.role.RoleResponse;
import com.chettra.devflow.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
	
	private final RoleService roleService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RoleResponse create(@Valid @RequestBody RoleRequest request) {
		return roleService.create(request);
	}
	
	@GetMapping("/{id}")
	public RoleResponse getById(@PathVariable Long id) {
		return roleService.getById(id);
	}
	
	@GetMapping
	public Page<RoleResponse> getAll(
			@PageableDefault(size = 10, sort = "name") Pageable pageable
	) {
		return roleService.getAll(pageable);
	}
	
	@PutMapping("/{id}")
	public RoleResponse update(
			@PathVariable Long id,
			@Valid @RequestBody RoleRequest request
	) {
		return roleService.update(id, request);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		roleService.delete(id);
	}
}
