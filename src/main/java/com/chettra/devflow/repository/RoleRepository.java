package com.chettra.devflow.repository;

import com.chettra.devflow.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
	
	Optional<Role> findByNameIgnoreCase(String name);
	
	boolean existsByNameIgnoreCase(String name);
}
