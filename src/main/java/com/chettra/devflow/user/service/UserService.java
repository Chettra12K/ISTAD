package com.chettra.devflow.user.service;

import com.chettra.devflow.user.dto.UserRequest;
import com.chettra.devflow.user.dto.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {
	UserResponse create(UserRequest user);
	Page<UserResponse> getAll(int page, int size);
	UserResponse findById(Long id);
	UserResponse Update(Long id, UserRequest request);
	void delete(Long id);
	Page<UserResponse> searchByUsername(String username, int page, int size);
}
