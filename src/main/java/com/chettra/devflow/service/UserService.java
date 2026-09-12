package com.chettra.devflow.service;

import com.chettra.devflow.dto.UserRequest;
import com.chettra.devflow.dto.UserResponse;
import com.chettra.devflow.entity.User;

import java.util.List;

public interface UserService {
	UserResponse create(UserRequest user);
	List<UserResponse> findAll();
	UserResponse findById(Long id);
	UserResponse Update(Long id, UserRequest user);
	void delete(Long id);
}
