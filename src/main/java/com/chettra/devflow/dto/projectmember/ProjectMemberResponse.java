package com.chettra.devflow.dto.projectmember;

import com.chettra.devflow.entity.ProjectMemberRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProjectMemberResponse {
    private Long id;
    private Long projectId;
    private Long userId;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private ProjectMemberRole role;
    private Boolean active;
    private LocalDateTime joinedAt;
}
