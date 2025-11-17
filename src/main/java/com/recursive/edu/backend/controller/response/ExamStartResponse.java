/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.response;

import com.recursive.edu.backend.model.dto.QuestionDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
@Data
@Builder
public class ExamStartResponse {
    private String attemptId;
    private String examId;
    private String examTitle;
    private int durationMinutes;
    private List<QuestionDTO> questions;
}
