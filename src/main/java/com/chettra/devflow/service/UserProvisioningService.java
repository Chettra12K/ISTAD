
package com.chettra.devflow.service;

import com.chettra.devflow.dto.user.UserProvisionRequest;
import com.chettra.devflow.dto.user.UserResponse;
import com.chettra.devflow.entity.User;
import com.chettra.devflow.mapper.UserMapper;
import com.chettra.devflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProvisioningService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakAdminClient keycloakAdminClient;

    public UserResponse provision(UserProvisionRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username is already registered"
            );
        }

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email is already registered"
            );
        }

        String keycloakId = null;

        try {
            keycloakId = keycloakAdminClient.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getFirstname(),
                    request.getLastname(),
                    request.getInitialPassword()
            );

            User user = User.builder()
                    .keycloakId(keycloakId)
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .profileImage(request.getProfileImage())
                    .active(true)
                    .build();

            // Flush now so database constraint failures are caught here.
            User saved = userRepository.saveAndFlush(user);
            return userMapper.mapToResponse(saved);

        } catch (RuntimeException failure) {
            if (keycloakId != null) {
                try {
                    keycloakAdminClient.deleteUser(keycloakId);
                } catch (RuntimeException cleanupFailure) {
                    // Log only the identity ID and exception, never credentials.
                    log.error(
                            "Provisioning compensation failed for Keycloak user ID {}",
                            keycloakId,
                            cleanupFailure
                    );
                }
            }

            if (failure instanceof ResponseStatusException responseFailure) {
                throw responseFailure;
            }

            if (failure instanceof DataIntegrityViolationException) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Username or email is already registered"
                );
            }

            if (failure instanceof RestClientResponseException remoteFailure) {
                HttpStatus status = remoteFailure.getStatusCode().value() == 409
                        ? HttpStatus.CONFLICT
                        : HttpStatus.BAD_GATEWAY;

                throw new ResponseStatusException(
                        status,
                        status == HttpStatus.CONFLICT
                                ? "Keycloak reports a duplicate user"
                                : "Keycloak provisioning request failed"
                );
            }

            log.error(
                    "User provisioning failed; Keycloak user ID: {}",
                    keycloakId,
                    failure
            );

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "User provisioning failed"
            );
        }
    }
}
