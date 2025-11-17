/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */
@Data
@Builder
public class ExamEnrollmentDetails {
    private String examUUID;
    private String title;
    private String description;
    private int durationMinutes;
    private int totalMarks;
    private Date startTime;
    private Date endTime;
}
