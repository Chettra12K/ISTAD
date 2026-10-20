
package com.chettra.devflow.service;

import com.chettra.devflow.dto.role.RoleRequest;
import com.chettra.devflow.dto.role.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
	
	RoleResponse create(RoleRequest request);
	
	RoleResponse getById(Long id);
	
	Page<RoleResponse> getAll(Pageable pageable);
	
	RoleResponse update(Long id, RoleRequest request);
	
	void delete(Long id);
}
