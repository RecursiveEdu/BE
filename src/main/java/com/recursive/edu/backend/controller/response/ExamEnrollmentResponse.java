/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */
@Data
@Builder
public class ExamEnrollmentResponse {
    private String enrollmentId;
    private String examTitle;
}
