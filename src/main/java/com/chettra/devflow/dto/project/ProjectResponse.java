package com.chettra.devflow.dto.project;

import com.chettra.devflow.entity.ProjectStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
	private Long id;
	private String name;
	private String description;
	private Boolean active;
	private ProjectStatus status;
	private Long ownerId;
	private String ownerUsername;
	private String createdAt;
	private String updatedAt;
}
