package com.chettra.devflow.repository;

import com.chettra.devflow.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByIdAndProjectId(Long id, Long projectId);
    boolean existsByProjectIdAndNameIgnoreCaseAndActiveTrue(Long projectId, String name);
}
