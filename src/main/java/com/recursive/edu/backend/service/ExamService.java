/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.service;

import com.recursive.edu.backend.controller.request.SubmitAnswerRequest;
import com.recursive.edu.backend.controller.response.ExamStartResponse;
import com.recursive.edu.backend.controller.response.SubmitAnswerResponse;
import org.apache.coyote.BadRequestException;

import java.util.List;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
public interface ExamService {
    /**
     * Starts an exam attempt by dynamically generating questions based on tags.
     * Randomizes question order, limits count, and uses batch inserts.
     */
    ExamStartResponse startExam(String examPrivateId, List<String> tagNames) throws BadRequestException;

    /**
     * Submit an answer to a question for an exam attempt.
     */
    SubmitAnswerResponse submitAnswer(SubmitAnswerRequest request);
}
