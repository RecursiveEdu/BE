/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import com.recursive.edu.backend.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailOrMobile(String email, String mobile);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET email_verified = true WHERE email = :id or mobile = :id", nativeQuery = true)
    int updateEmailVerified(@Param("id") String id);
}
