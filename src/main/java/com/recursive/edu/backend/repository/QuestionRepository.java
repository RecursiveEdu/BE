package com.recursive.edu.backend.repository;

import com.recursive.edu.backend.model.postgres.Question;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Cacheable(value = "questionsByTags", key = "#tagNames")
    @Query("""
        SELECT q FROM Question q
        JOIN q.tags t
        WHERE t.name IN :tagNames
        GROUP BY q.id
        HAVING COUNT(DISTINCT t.id) = :tagCount
    """)
    List<Question> findQuestionsByAllTags(@Param("tagNames") List<String> tagNames, @Param("tagCount") long tagCount);
}
