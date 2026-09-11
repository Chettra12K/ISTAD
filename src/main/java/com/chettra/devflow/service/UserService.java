package com.chettra.devflow.service;

import com.chettra.devflow.entity.User;

import java.util.List;

public interface UserService {
	User create(User user);
	List<User> findAll();
	User findById(Long id);
	User Update(Long id, User user);
	void delete(Long id);
}
