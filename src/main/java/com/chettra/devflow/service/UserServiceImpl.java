package com.chettra.devflow.service;

import com.chettra.devflow.dto.user.UserRequest;
import com.chettra.devflow.dto.user.UserResponse;
import com.chettra.devflow.entity.Role;
import com.chettra.devflow.entity.User;
import com.chettra.devflow.common.exception.ResourceNotFoundException;
import com.chettra.devflow.mapper.UserMapper;
import com.chettra.devflow.repository.RoleRepository;
import com.chettra.devflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final RoleRepository roleRepository;


	@Override
	public UserResponse create (UserRequest request) {

		User user = userMapper.mapToEntity(request);
		User savedUser = userRepository.save(user);
		return userMapper.mapToResponse(savedUser);
	}

	@Override
	public UserResponse findById(Long id) {
		User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found with id:" + id));
		return userMapper.mapToResponse(user);
	}

	@Override
	public Page<UserResponse> getAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		return userRepository.findAll(pageable).map(userMapper::mapToResponse);
	}

	@Override
	public UserResponse Update(Long id, UserRequest request) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + id));
		userMapper.updateEntity(request, user);
		User updatedUser = userRepository.save(user);
		return userMapper.mapToResponse(updatedUser);
	}
	
	@Override
	public void delete(Long id) {
		User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found with id:" + id));
		userRepository.deleteById(id);
	}

	@Override
	public Page<UserResponse> searchByUsername(String username,int page, int size){
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		return userRepository.findByUsernameContainingIgnoreCase(username,pageable).map(userMapper::mapToResponse);
	}
	@Override
	@Transactional
	public void assignRole(Long userId, Long roleId) {

		User user = userRepository.findByIdWithRoles(userId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"User not found with id: " + userId
				));

		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Role not found with id: " + roleId
				));

		if (!user.getRoles().add(role)) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"User already has role: " + role.getName()
			);
		}
	}
	@Override
	@Transactional
	public void removeRole(Long userId, Long roleId) {

		User user = userRepository.findByIdWithRoles(userId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"User not found with id: " + userId
				));

		Role role = roleRepository.findById(roleId)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Role not found with id: " + roleId
				));

		if (!user.getRoles().remove(role)) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"User does not have role: " + role.getName()
			);
		}
	}

}
