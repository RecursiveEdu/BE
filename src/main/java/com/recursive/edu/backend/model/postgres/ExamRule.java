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
@Table(name = "exam_rules", indexes = {
        @Index(name = "idx_exam_rules_exam", columnList = "exam_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamRule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @Column(name = "tag_name")
    private String tagName;

    private String difficulty;

    @Column(name = "question_count")
    private int questionCount = 1;
}

