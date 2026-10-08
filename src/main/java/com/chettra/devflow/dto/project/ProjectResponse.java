package com.chettra.devflow.dto.project;

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
	private Long ownerId;
	private String OwnerUserName;
	private String createdAt;
	private String updatedAt;
}
