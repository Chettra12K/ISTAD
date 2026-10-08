package com.chettra.devflow.mapper;

import com.chettra.devflow.dto.project.ProjectResponse;
import com.chettra.devflow.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper (componentModel = "spring")
public interface ProjectMapper {
	@Mapping( target = "ownerId", source = "owner.id")
	@Mapping(target = "OwnerUserName", source = "owner.username")
	ProjectResponse toResponse(Project project);
}
