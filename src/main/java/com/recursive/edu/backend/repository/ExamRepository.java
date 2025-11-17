/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.repository;

import com.recursive.edu.backend.model.postgres.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByPublicIdAndPublishedTrue(String publicId);
}
