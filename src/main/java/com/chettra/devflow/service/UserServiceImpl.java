package com.chettra.devflow.service;

import com.chettra.devflow.entity.User;
import com.chettra.devflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	
	@Override
	public User create(User user) {
		return userRepository.save(user);
	}
	
	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}
	
	@Override
	public User findById(Long id) {
		return userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found with id:" + id));
	}
	
	@Override
	public User Update(Long id, User user) {
		User existingUser = findById(id);
		existingUser.setUsername(user.getUsername());
		existingUser.setEmail(user.getEmail());
		existingUser.setFirstname(user.getFirstname());
		existingUser.setLastname(user.getLastname());
		existingUser.setPassword(user.getPassword());
		return userRepository.save(existingUser);
	}
	
	@Override
	public void delete(Long id) {
		
		User existingUser = findById(id);
		userRepository.delete(existingUser);
	
	}
}
