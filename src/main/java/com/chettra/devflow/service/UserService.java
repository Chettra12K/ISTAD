package com.chettra.devflow.service;

import com.chettra.devflow.dto.UserRequest;
import com.chettra.devflow.dto.UserResponse;
import com.chettra.devflow.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
	UserResponse create(UserRequest user);
	Page<UserResponse> getAll(int page, int size);
	UserResponse findById(Long id);
	UserResponse Update(Long id, UserRequest request);
	void delete(Long id);
	Page<UserResponse> searchByUsername(String username, int page, int size);
}
