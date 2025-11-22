package com.recursive.edu.backend.repository;

import com.recursive.edu.backend.model.postgres.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author PrantikGuha
 * CreatedAt: {22-11-2025}
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByNameIgnoreCase(String name);

    @Query("""
        SELECT t FROM Tag t 
        WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Tag> findSimilarTags(String keyword);
}

