/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
@AllArgsConstructor
@Getter
public enum ErrorCodes {
    EXAM_COMPLETED_ANSWER_SUBMISSION_NOT_ALLOWED(901, "Exam already completed, answer submission not allowed.");

    private int errorCode;
    private String errorMessage;
}
