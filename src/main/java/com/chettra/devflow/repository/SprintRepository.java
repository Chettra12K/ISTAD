package com.chettra.devflow.repository;

import com.chettra.devflow.entity.Sprint;
import com.chettra.devflow.entity.SprintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
    Optional<Sprint> findByIdAndProjectId(Long id, Long projectId);
    Page<Sprint> findByProjectId(Long projectId, Pageable pageable);
    Page<Sprint> findByProjectIdAndStatus(Long projectId, SprintStatus status, Pageable pageable);
    boolean existsByProjectIdAndNameIgnoreCase(Long projectId, String name);
}
