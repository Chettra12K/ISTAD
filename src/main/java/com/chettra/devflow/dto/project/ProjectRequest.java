package com.chettra.devflow.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {
	@NotBlank(message = "Project name is required")
	@Size(min = 3, max = 150, message = "Project name must be between 3 and 150 characters")
	private String name;
	
	@Size(max = 1000, message = "Description cannot exceed 1000 characters")
	private String description;
	
	@NotNull(message = "Owner ID is required")
	private Long ownerId;
}
