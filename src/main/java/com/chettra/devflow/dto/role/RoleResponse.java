
package com.chettra.devflow.dto.role;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RoleResponse {
	
	private Long id;
	private String name;
	private String description;
	private LocalDateTime createdAt;
}
