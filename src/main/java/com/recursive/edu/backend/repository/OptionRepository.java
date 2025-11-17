/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.repository;

import com.recursive.edu.backend.model.postgres.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    Optional<Option> findByPublicIdAndQuestionPublicId(String publicId, String questionPublicId);
}
