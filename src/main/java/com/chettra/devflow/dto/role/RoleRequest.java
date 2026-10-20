
package com.chettra.devflow.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleRequest {
	
	@NotBlank(message = "Role name is required")
	@Size(max = 50, message = "Role name must not exceed 50 characters")
	@Pattern(
			regexp = "ROLE_[A-Z][A-Z0-9_]*",
			message = "Role name must start with ROLE_ and contain uppercase letters, numbers, or underscores"
	)
	private String name;
	
	@Size(max = 255, message = "Description must not exceed 255 characters")
	private String description;
}
