/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.postgres;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */

@Entity
@Table(name = "attempt_answers", indexes = {
        @Index(name = "idx_attempt_answers_attempt_id", columnList = "attempt_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttemptAnswer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id")
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private Option selectedOption;

    @Column(columnDefinition = "text", name = "answer_payload")
    private Object answerPayload; // structured answer

    private Boolean isCorrect;

    @Column(name = "score_earned")
    private int scoreEarned = 0;
}
