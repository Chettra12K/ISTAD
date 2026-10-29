
package com.chettra.devflow.service;

import com.chettra.devflow.entity.Project;
import com.chettra.devflow.entity.ProjectMember;
import com.chettra.devflow.entity.User;
import com.chettra.devflow.repository.ProjectMemberRepository;
import com.chettra.devflow.repository.ProjectRepository;
import com.chettra.devflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("projectAuthorizationService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectAuthorizationService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public boolean canManageProject(
            Long projectId,
            Authentication authentication) {

        if (hasRole(authentication, "ADMIN")) {
            return true;
        }

        if (!hasRole(authentication, "PROJECT_MANAGER")) {
            return false;
        }

        User user = findAuthenticatedUser(authentication);
        if (user == null) {
            return false;
        }

        Project project = projectRepository.findByIdAndActiveTrue(projectId)
                .orElse(null);

        if (project == null) {
            return false;
        }

        // Project owners can manage their own projects.
        if (project.getOwner().getId().equals(user.getId())) {
            return true;
        }

        // An active project membership with MANAGER role grants access.
        return projectMemberRepository
                .findByProjectIdAndUserId(projectId, user.getId())
                .filter(ProjectMember::getActive)
                .map(member -> member.getRole().name().equals("MANAGER"))
                .orElse(false);
    }

    private boolean hasRole(
            Authentication authentication,
            String role) {

        return authentication != null
                && authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_" + role));
    }

    private User findAuthenticatedUser(
            Authentication authentication) {

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return null;
        }

        String keycloakId = jwtAuth.getToken().getSubject();

        if (keycloakId == null || keycloakId.isBlank()) {
            return null;
        }

        return userRepository.findByKeycloakId(keycloakId)
                .filter(User::getActive)
                .orElse(null);
    }

    public boolean canCreateProjectForOwner(
            Long ownerId,
            Authentication authentication) {

        if (hasRole(authentication, "ADMIN")) {
            return true;
        }

        if (!hasRole(authentication, "PROJECT_MANAGER")) {
            return false;
        }

        User authenticatedUser = findAuthenticatedUser(authentication);

        return authenticatedUser != null
                && authenticatedUser.getId().equals(ownerId);
    }

    public boolean canUpdateProjectOwner(
            Long projectId,
            Long requestedOwnerId,
            Authentication authentication) {

        if (hasRole(authentication, "ADMIN")) {
            return true;
        }

        if (!canManageProject(projectId, authentication)) {
            return false;
        }

        Project project = projectRepository.findByIdAndActiveTrue(projectId)
                .orElse(null);

        // Non-admins cannot transfer project ownership.
        return project != null
                && project.getOwner().getId().equals(requestedOwnerId);
    }
}
