package com.chettra.devflow.dto.projectmember;

import com.chettra.devflow.entity.ProjectMemberRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProjectMemberRoleRequest {

    @NotNull(message = "Member role is required")
    private ProjectMemberRole role;
}
