/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.repository;

import com.recursive.edu.backend.model.postgres.Enrollment;
import com.recursive.edu.backend.model.postgres.Exam;
import com.recursive.edu.backend.model.postgres.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByUserAndExam(User user, Exam exam);

    List<Enrollment> findByUserPublicIdAndExamPublishedTrue(String userPrivateId);

    @EntityGraph(attributePaths = {"exam"})
    Page<Enrollment> findByUserPublicIdAndExamPublishedTrue(String userPrivateId, Pageable pageable);

    Optional<Enrollment> findByUserPublicIdAndExamPublicId(String userUuid, String examPrivateId);
}
