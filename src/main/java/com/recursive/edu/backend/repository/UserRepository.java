package com.recursive.edu.backend.repository;

import com.recursive.edu.backend.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
