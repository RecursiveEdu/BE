/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.postgres;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */
@Entity
@Table(name = "attempt_questions", indexes = {
        @Index(name = "idx_attempt_questions_attempt_id", columnList = "attempt_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@BatchSize(size = 20)
public class AttemptQuestion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "marks_assigned")
    private int marksAssigned;

    @Column(name = "order_no")
    private int orderNo;
}
