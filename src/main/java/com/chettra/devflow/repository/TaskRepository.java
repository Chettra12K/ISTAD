package com.chettra.devflow.repository;

import com.chettra.devflow.entity.Task;
import com.chettra.devflow.entity.TaskPriority;
import com.chettra.devflow.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndProjectId(Long id, Long projectId);

    Optional<Task> findTopByProjectIdOrderByTaskNumberDesc(Long projectId);

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    Page<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);

    Page<Task> findByProjectIdAndPriority(Long projectId, TaskPriority priority, Pageable pageable);

    Page<Task> findByProjectIdAndAssigneeId(Long projectId, Long assigneeId, Pageable pageable);
}
