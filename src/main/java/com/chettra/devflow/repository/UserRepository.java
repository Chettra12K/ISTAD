package com.chettra.devflow.repository;

import com.chettra.devflow.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
//    Optional<User> findByEmail(String email);
//    Optional<User> findByUsername(String username);
//    boolean existsByUsername(String username);
//    boolean existsByEmail(String email);
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
