package com.chettra.devflow.repository;

import com.chettra.devflow.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndTaskId(Long id, Long taskId);
    Page<Comment> findByTaskId(Long taskId, Pageable pageable);
}
