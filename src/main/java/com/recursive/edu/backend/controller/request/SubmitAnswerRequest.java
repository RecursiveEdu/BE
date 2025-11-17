/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.request;

import lombok.Data;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
@Data
public class SubmitAnswerRequest {
    private String examAttemptId;
    private String questionId;
    private String optionId;
    private String questionType;
    private String answer;
}

