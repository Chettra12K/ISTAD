package com.chettra.devflow.service;

import com.chettra.devflow.dto.UserRequest;
import com.chettra.devflow.dto.UserResponse;
import com.chettra.devflow.entity.User;
import com.chettra.devflow.exception.ResourceNotFoundException;
import com.chettra.devflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;

	private UserResponse mapTopResponse(User user) {
		return UserResponse.builder().id(user.getId()).username(user.getUsername()).email(user.getEmail()).build();
	}
	@Override
	public UserResponse create(UserRequest request) {

		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());

		User savedUser = userRepository.save(user);
		return mapTopResponse(savedUser);
	}
	
	@Override
	public List<UserResponse> findAll() {
		return userRepository.findAll().stream().map(this::mapTopResponse).toList();
	}
	
	@Override
	public UserResponse findById(Long id) {
		User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found with id:" + id));
		return mapTopResponse(user);
	}
	
	@Override
	public UserResponse Update(Long id, UserRequest request) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + id));

			user.setUsername(request.getUsername());
			user.setEmail(request.getEmail());

			User updatedUser = userRepository.save(user);
			return mapTopResponse(updatedUser);

	}
	
	@Override
	public void delete(Long id) {

		if (!userRepository.existsById(id)) {throw new ResourceNotFoundException("User not found with id:" + id);}
		userRepository.deleteById(id);
	}
}
