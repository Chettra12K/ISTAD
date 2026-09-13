package com.chettra.devflow.controller;

import com.chettra.devflow.config.ApiConstrants;
import com.chettra.devflow.dto.UserRequest;
import com.chettra.devflow.dto.UserResponse;
import com.chettra.devflow.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiConstrants.API_V1 + "/users")
@Validated
public class UserController {
	
	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.findById(id));
	}

	@GetMapping
	public ResponseEntity<Page<UserResponse>> getAll(
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
		return ResponseEntity.ok(userService.getAll(page,size));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> update(
			@PathVariable Long id,
			@RequestBody UserRequest request) {
		return ResponseEntity.ok(userService.Update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/search")
	public ResponseEntity<Page<UserResponse>> searchByUsername(
			@RequestParam String name,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
		return ResponseEntity.ok(userService.searchByUsername(name, page, size));
	}
}
