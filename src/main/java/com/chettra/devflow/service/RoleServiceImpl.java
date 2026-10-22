
package com.chettra.devflow.service;

import com.chettra.devflow.dto.role.RoleRequest;
import com.chettra.devflow.dto.role.RoleResponse;
import com.chettra.devflow.entity.Role;
import com.chettra.devflow.mapper.RoleMapper;
import com.chettra.devflow.repository.RoleRepository;
import com.chettra.devflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
	
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final RoleMapper roleMapper;
	
	@Override
	@Transactional
	public RoleResponse create(RoleRequest request) {
		String name = normalizeName(request.getName());
		
		if (roleRepository.existsByNameIgnoreCase(name)) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"Role already exists: " + name
			);
		}
		
		Role role = roleMapper.mapToEntity(request);
		role.setName(name);
		
		return saveAndMap(role);
	}
	
	@Override
	public RoleResponse getById(Long id) {
		return roleMapper.mapToResponse(findRole(id));
	}
	
	@Override
	public Page<RoleResponse> getAll(Pageable pageable) {
		return roleRepository.findAll(pageable)
				.map(roleMapper::mapToResponse);
	}
	
	@Override
	@Transactional
	public RoleResponse update(Long id, RoleRequest request) {
		Role role = findRole(id);
		String name = normalizeName(request.getName());
		
		boolean duplicate = roleRepository.findByNameIgnoreCase(name)
				.filter(existing -> !existing.getId().equals(id))
				.isPresent();
		
		if (duplicate) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"Role already exists: " + name
			);
		}
		
		role.setName(name);
		role.setDescription(request.getDescription());
		
		return saveAndMap(role);
	}
	
	@Override
	@Transactional
	public void delete(Long id) {
		Role role = findRole(id);
		
		if (userRepository.existsByRoles_Id(id)) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"Cannot delete a role that is assigned to users"
			);
		}
		
		roleRepository.delete(role);
	}
	
	private Role findRole(Long id) {
		return roleRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Role not found with id: " + id
				));
	}
	
	private String normalizeName(String name) {
		if (name == null || name.isBlank()) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Role name is required"
			);
		}
		
		return name.trim().toUpperCase(Locale.ROOT);
	}
	
	private RoleResponse saveAndMap(Role role) {
		try {
			return roleMapper.mapToResponse(roleRepository.saveAndFlush(role));
		} catch (DataIntegrityViolationException exception) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"Role conflicts with an existing database record",
					exception
			);
		}
	}
}
