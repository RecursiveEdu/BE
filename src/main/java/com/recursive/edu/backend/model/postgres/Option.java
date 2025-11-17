/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.postgres;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */
@Entity
@Table(name = "options", indexes = {
        @Index(name = "idx_options_question_id", columnList = "question_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private String publicId = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(columnDefinition = "text", nullable = false)
    private String optionText;

    @Column(name = "is_correct")
    private boolean correct;
}
