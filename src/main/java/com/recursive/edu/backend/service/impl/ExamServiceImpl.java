package com.recursive.edu.backend.service.impl;

import com.recursive.edu.backend.controller.request.SubmitAnswerRequest;
import com.recursive.edu.backend.controller.response.ExamStartResponse;
import com.recursive.edu.backend.controller.response.SubmitAnswerResponse;
import com.recursive.edu.backend.model.common.ErrorCodes;
import com.recursive.edu.backend.model.dto.OptionDTO;
import com.recursive.edu.backend.model.dto.QuestionDTO;
import com.recursive.edu.backend.model.exam.AttemptStatus;
import com.recursive.edu.backend.model.exam.QuestionType;
import com.recursive.edu.backend.model.exception.ExamFinishedException;
import com.recursive.edu.backend.model.exception.ResourceNotFoundException;
import com.recursive.edu.backend.model.postgres.*;
import com.recursive.edu.backend.repository.*;
import com.recursive.edu.backend.service.ExamService;
import com.recursive.edu.backend.util.DateUtil;
import com.recursive.edu.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final AttemptQuestionRepository attemptQuestionRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final OptionRepository optionRepository;

    @Value("${exam.default-question-limit:20}")
    private int defaultQuestionLimit;

    @Override
    @Transactional
    public ExamStartResponse startExam(String examPrivateId, List<String> tagNames) throws BadRequestException {
        try {
            String userUUID = SecurityUtils.getCurrentUserUuid();

            log.info("Starting exam [{}] for user [{}] using tags {}", examPrivateId, userUUID, tagNames);

            // 1️⃣ Validate exam
            Exam exam = examRepository.findByPublicIdAndPublishedTrue(examPrivateId)
                    .orElseThrow(() -> new ResourceNotFoundException("Exam not found or not published"));

            // 2️⃣ Validate enrollment
            Enrollment enrollment = enrollmentRepository
                    .findByUserPublicIdAndExamPublicId(userUUID, examPrivateId)
                    .orElseThrow(() -> new BadRequestException("User is not enrolled for this exam"));

            // 3️⃣ Check if already attempted
            Optional<ExamAttempt> existingAttempt = examAttemptRepository
                    .findByEnrollmentUserPublicIdAndEnrollmentExamPublicId(userUUID, examPrivateId);

            if (existingAttempt.isPresent()) {
                throw new BadRequestException("Exam already started");
            }

            // 4️⃣ Generate dynamic questions
            List<Question> questions = questionRepository.findQuestionsByAllTags(tagNames, tagNames.size());

            if (questions.isEmpty()) {
                throw new BadRequestException("No questions available for this exam");
            }

            // ✅ 5. Shuffle and limit questions (random selection)
            Collections.shuffle(questions);
            int limit = Math.min(defaultQuestionLimit, questions.size());
            List<Question> selectedQuestions = questions.subList(0, limit);

            // 6. Create ExamAttempt entity
            ExamAttempt attempt = ExamAttempt.builder()
                    .enrollment(enrollment)
                    .startedAt(new Date())
                    .status(AttemptStatus.IN_PROGRESS.name())
                    .build();
            examAttemptRepository.save(attempt);

            // 7. Map question attempts
            List<AttemptQuestion> questionAttempts = questions.stream()
                    .map(q -> AttemptQuestion.builder()
                            .attempt(attempt)
                            .question(q)
                            .build())
                    .toList();
            attemptQuestionRepository.saveAllAndFlush(questionAttempts);
            log.info("Created {} question attempts for exam attempt ID={}", questionAttempts.size(), attempt.getId());

            // 7️⃣ Build response DTO
            List<QuestionDTO> questionDTOs = questions.stream()
                    .map(q -> QuestionDTO.builder()
                            .questionId(q.getPublicId().toString())
                            .text(q.getQuestionText())
                            .options(q.getOptions().stream()
                                    .map(option -> OptionDTO.builder()
                                            .optionPublicId(option.getPublicId().toString())
                                            .optionText(option.getOptionText())
                                            .build()
                                    ).toList())
                            .type(q.getQuestionType())
                            .build())
                    .toList();

            return ExamStartResponse.builder()
                    .attemptId(attempt.getPublicId().toString())
                    .examId(exam.getPublicId().toString())
                    .examTitle(exam.getTitle())
                    .durationMinutes(exam.getDurationMinutes())
                    .questions(questionDTOs)
                    .build();
        } catch (Exception exception) {
            log.error("Exception in ExamServiceImpl.startExam: {}", exception.getMessage(), exception);
            throw exception;
        }
    }

    @Override
    @Transactional
    public SubmitAnswerResponse submitAnswer(SubmitAnswerRequest request) {
        try {
            String userUuid = SecurityUtils.getCurrentUserUuid();

            // ✅ 2. Validate attempt belongs to user
            Optional<ExamAttempt> examAttempt = examAttemptRepository.findByEnrollmentUserPublicIdAndEnrollmentExamPublicId(userUuid, request.getExamAttemptId());
            if (examAttempt.isEmpty()) {
                throw new ResourceNotFoundException("Invalid exam attempt.");
            } else {
                Exam exam = examAttempt.get().getEnrollment().getExam();
                if (examAttempt.get().isCompleted() || Objects.equals(examAttempt.get().getStatus(), AttemptStatus.COMPLETED.name())) {
                    throw new ExamFinishedException("Exam is already finished.");
                }
                if (System.currentTimeMillis() - examAttempt.get().getStartedAt().getTime() > DateUtil.getMillis(exam.getDurationMinutes())) {
                    // save the exam attempt as completed
                    throw new ExamFinishedException("Exam is already finished.");
                }
                // ✅ 3. Find question attempt within this exam
                AttemptQuestion questionAttempt = attemptQuestionRepository
                        .findByAttemptPublicIdAndQuestionPublicId(request.getExamAttemptId(), request.getQuestionId())
                        .orElseThrow(() -> new IllegalArgumentException("Question not found in this attempt"));

                // ✅ 4. Upsert (save or update) answer
                AttemptAnswer answerAttempt = attemptAnswerRepository
                        .findByAttemptPublicIdAndQuestionPublicId(request.getExamAttemptId(), request.getQuestionId())
                        .orElse(new AttemptAnswer());
                answerAttempt.setAttempt(examAttempt.get());
                answerAttempt.setQuestion(questionAttempt.getQuestion());
                if (QuestionType.SHORT_ANSWER.name().equals(questionAttempt.getQuestion().getQuestionType())) {
                    answerAttempt.setAnswerPayload(request.getAnswer());
                } else {
                    Option option = optionRepository.findByPublicIdAndQuestionPublicId(request.getOptionId(), request.getQuestionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Option is not associated with the question."));
                    answerAttempt.setSelectedOption(option);
                }
                attemptAnswerRepository.saveAndFlush(answerAttempt);
                return new SubmitAnswerResponse(200, "Answer submitted successfully.");
            }
        } catch (ExamFinishedException exception) {
            return new SubmitAnswerResponse(ErrorCodes.EXAM_COMPLETED_ANSWER_SUBMISSION_NOT_ALLOWED.getErrorCode(),
                    ErrorCodes.EXAM_COMPLETED_ANSWER_SUBMISSION_NOT_ALLOWED.getErrorMessage());
        } catch (Exception exception) {
            log.error("Exception in ExamServiceImpl.submitAnswer: {}", exception.getMessage(), exception);
            throw exception;
        }
    }
}
