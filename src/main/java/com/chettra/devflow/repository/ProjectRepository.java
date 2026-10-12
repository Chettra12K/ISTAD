package com.chettra.devflow.repository;

import com.chettra.devflow.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository <Project, Long>, JpaSpecificationExecutor<Project> {

	Page<Project> findByActiveTrue(Pageable pageable);
	Page<Project> findByOwnerIdAndActiveTrue(Long ownerId, Pageable pageable);
	Page<Project> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);
	Optional<Project> findByIdAndActiveTrue(Long id);
	Boolean existsByNameIgnoreCaseAndOwnerId(String name, Long ownerId);
}
