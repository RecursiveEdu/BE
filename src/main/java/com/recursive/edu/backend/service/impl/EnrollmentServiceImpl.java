/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.service.impl;

import com.recursive.edu.backend.controller.response.ExamEnrollmentDetails;
import com.recursive.edu.backend.controller.response.ExamEnrollmentResponse;
import com.recursive.edu.backend.controller.response.PageableContent;
import com.recursive.edu.backend.model.exception.DuplicateResourceException;
import com.recursive.edu.backend.model.exception.ResourceNotFoundException;
import com.recursive.edu.backend.model.postgres.Enrollment;
import com.recursive.edu.backend.model.postgres.Exam;
import com.recursive.edu.backend.model.postgres.User;
import com.recursive.edu.backend.repository.EnrollmentRepository;
import com.recursive.edu.backend.repository.ExamRepository;
import com.recursive.edu.backend.repository.UserRepository;
import com.recursive.edu.backend.service.EnrollmentService;
import com.recursive.edu.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author PrantikGuha
 * CreatedAt: {12-11-2025}
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    @Override
    public ExamEnrollmentResponse enrollUser(String examUUID) {
        try {
            String userUUID = SecurityUtils.getCurrentUserUuid();
            User user = userRepository.findByPublicId(userUUID)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            Exam exam = examRepository.findByPublicIdAndPublishedTrue(examUUID)
                    .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

            boolean alreadyEnrolled = enrollmentRepository.existsByUserAndExam(user, exam);
            if (alreadyEnrolled) {
                throw new DuplicateResourceException("User already enrolled in this exam");
            }
            Enrollment enrollment = Enrollment.builder()
                    .user(user)
                    .exam(exam)
                    .build();
            enrollmentRepository.saveAndFlush(enrollment);
            return ExamEnrollmentResponse.builder()
                    .enrollmentId(enrollment.getPublicId().toString())
                    .examTitle(exam.getTitle())
                    .build();
        } catch (Exception exception) {
            log.error("Exception in EnrollmentServiceImpl.enrollUser: {}", exception.getMessage());
            throw exception;
        }
    }

    @Override
    public PageableContent<List<ExamEnrollmentDetails>> getAllUserEnrollments(int page, int size) {
        try {
            String userUUID = SecurityUtils.getCurrentUserUuid();

            // Build pageable object (sort by enrolledAt descending)
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "enrolledAt"));
            Page<Enrollment> enrollmentsPage = enrollmentRepository.findByUserPublicIdAndExamPublishedTrue(userUUID, pageable);

            List<ExamEnrollmentDetails> details = enrollmentsPage.getContent()
                    .stream().map(enrollment -> ExamEnrollmentDetails.builder()
                            .title(enrollment.getExam().getTitle())
                            .description(enrollment.getExam().getDescription())
                            .examUUID(enrollment.getExam().getPublicId().toString())
                            .startTime(enrollment.getExam().getStartTime())
                            .endTime(enrollment.getExam().getEndTime())
                            .totalMarks(enrollment.getExam().getTotalMarks())
                            .durationMinutes(enrollment.getExam().getDurationMinutes())
                            .build())
                    .toList();

            return PageableContent.<List<ExamEnrollmentDetails>>builder()
                    .data(details)
                    .page(enrollmentsPage.getNumber())
                    .totalElements(enrollmentsPage.getTotalElements())
                    .size(enrollmentsPage.getSize())
                    .totalPages(enrollmentsPage.getTotalPages())
                    .build();
        } catch (Exception exception) {
            log.error("Exception in EnrollmentServiceImpl.getAllUserEnrollments: {}", exception.getMessage());
            throw exception;
        }
    }
}
