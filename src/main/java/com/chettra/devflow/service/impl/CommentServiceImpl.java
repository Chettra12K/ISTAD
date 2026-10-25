package com.chettra.devflow.service.impl;

import com.chettra.devflow.common.exception.ResourceNotFoundException;
import com.chettra.devflow.dto.comment.CommentRequest;
import com.chettra.devflow.dto.comment.CommentResponse;
import com.chettra.devflow.entity.Comment;
import com.chettra.devflow.entity.Task;
import com.chettra.devflow.entity.User;
import com.chettra.devflow.repository.CommentRepository;
import com.chettra.devflow.repository.TaskRepository;
import com.chettra.devflow.repository.UserRepository;
import com.chettra.devflow.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public CommentResponse create(Long projectId, Long taskId, String username, CommentRequest request) {
        Task task = getTask(projectId, taskId);
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user is not registered in DevFlow"));

        Comment comment = Comment.builder()
                .task(task)
                .user(user)
                .content(normalize(request.getContent()))
                .build();

        return toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getAll(Long projectId, Long taskId, Pageable pageable) {
        getTask(projectId, taskId);
        return commentRepository.findByTaskId(taskId, pageable).map(this::toResponse);
    }

    @Override
    public CommentResponse update(Long projectId, Long taskId, Long commentId, CommentRequest request) {
        getTask(projectId, taskId);
        Comment comment = commentRepository.findByIdAndTaskId(commentId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));
        comment.setContent(normalize(request.getContent()));
        return toResponse(commentRepository.save(comment));
    }

    @Override
    public void delete(Long projectId, Long taskId, Long commentId) {
        getTask(projectId, taskId);
        Comment comment = commentRepository.findByIdAndTaskId(commentId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));
        commentRepository.delete(comment);
    }

    private Task getTask(Long projectId, Long taskId) {
        return taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
    }

    private String normalize(String content) {
        return content.trim();
    }

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .taskId(comment.getTask().getId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
