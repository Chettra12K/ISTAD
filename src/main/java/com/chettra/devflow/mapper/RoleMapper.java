
package com.chettra.devflow.mapper;

import com.chettra.devflow.dto.role.RoleRequest;
import com.chettra.devflow.dto.role.RoleResponse;
import com.chettra.devflow.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
	
	RoleResponse mapToResponse(Role role);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	Role mapToEntity(RoleRequest request);
}
