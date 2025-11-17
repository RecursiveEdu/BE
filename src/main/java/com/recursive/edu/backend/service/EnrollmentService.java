/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.service;

import com.recursive.edu.backend.controller.response.ExamEnrollmentDetails;
import com.recursive.edu.backend.controller.response.ExamEnrollmentResponse;
import com.recursive.edu.backend.controller.response.PageableContent;

import java.util.List;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */
public interface EnrollmentService {
    ExamEnrollmentResponse enrollUser(String examUUID);
    /**
     * Fetch paginated list of a user's published exam enrollments.
     *
     * the UUID of the user from security
     * @param page current page (0-based)
     * @param size number of records per page
     * @return Result with paginated enrollment data
     */
    PageableContent<List<ExamEnrollmentDetails>> getAllUserEnrollments(int page, int size);
}
