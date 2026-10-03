package com.chettra.devflow.user.repository;

import com.chettra.devflow.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByEmail(String email);
//    Optional<User> findByUsername(String username);
//    boolean existsByUsername(String username);
//    boolean existsByEmail(String email);
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
