package com.chettra.devflow.service;

import com.chettra.devflow.dto.comment.CommentRequest;
import com.chettra.devflow.dto.comment.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentResponse create(Long projectId, Long taskId, String username, CommentRequest request);
    Page<CommentResponse> getAll(Long projectId, Long taskId, Pageable pageable);
    CommentResponse update(Long projectId, Long taskId, Long commentId, CommentRequest request);
    void delete(Long projectId, Long taskId, Long commentId);
}
