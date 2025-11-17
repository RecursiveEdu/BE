/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.postgres;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */
@Entity
@Table(name = "exam_attempts", indexes = {
        @Index(name = "idx_attempts_enrollment_id", columnList = "enrollment_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@BatchSize(size = 20)
public class ExamAttempt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private String publicId = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @CreatedDate
    @Column(name = "started_at", nullable = false, updatable = false)
    private Date startedAt;

    @Column(name = "submitted_at")
    private Date submittedAt;

    @Column(name = "total_score")
    private int totalScore = 0;

    @Column(name = "is_completed")
    private boolean isCompleted = false;

    @Version
    private Long version;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttemptQuestion> attemptQuestions;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttemptAnswer> attemptAnswers;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "status")
    private String status;
}
