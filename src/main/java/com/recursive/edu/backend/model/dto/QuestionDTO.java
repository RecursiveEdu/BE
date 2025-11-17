/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
@Data
@Builder
public class QuestionDTO {
    private String questionId;
    private String text;
    private String type; // MCQ, SHORT_ANSWER, etc.
    private List<OptionDTO> options; // JSON array or List<String>
}
