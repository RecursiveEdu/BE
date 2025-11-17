/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.repository;

import com.recursive.edu.backend.model.postgres.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
@Repository
public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long> {
    Optional<AttemptAnswer> findByAttemptPublicIdAndQuestionPublicId(String examAttemptPublicId, String questionPublicId);
}
