package com.harshbisht.ExamService.service;

import com.harshbisht.ExamService.dto.ExamDTO.CreateExamRequest;
import com.harshbisht.ExamService.dto.ExamDTO.EditExamRequest;
import com.harshbisht.ExamService.dto.ExamDTO.ExamResponse;
import com.harshbisht.ExamService.dto.OptionDTO.OptionEditRequest;
import com.harshbisht.ExamService.dto.QuestionDTO.QuestionEditRequest;
import com.harshbisht.ExamService.entity.ExamEntity;
import com.harshbisht.ExamService.entity.OptionEntity;
import com.harshbisht.ExamService.entity.QuestionEntity;
import com.harshbisht.ExamService.entity.SubjectEntity;
import com.harshbisht.ExamService.repository.ExamRepository;
import com.harshbisht.ExamService.repository.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;

    // 🔹 Get exams by subject → return DTOs
    public List<ExamResponse> getExamsBySubject(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new RuntimeException("Subject Not Found");
        }
        return examRepository.findBySubject_Id(subjectId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Create exam → return DTO
    public ExamResponse createExam(CreateExamRequest request) {

        Long teacherId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        SubjectEntity subject = subjectRepository
                .findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        ExamEntity exam = new ExamEntity();
        exam.setTitle(request.getTitle());
        exam.setSubject(subject);
        exam.setTeacherId(teacherId);
        exam.setPublished(false);

        examRepository.save(exam);

        return toResponse(exam);
    }


    // 🔹 Delete exam
    public void deleteExam(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (exam.isPublished()) {
            throw new RuntimeException("Cannot delete a published exam");
        }
        examRepository.delete(exam);
    }

    // 🔹 Publish exam → return DTO
    public ExamResponse publishExam(Long examId) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        exam.setPublished(true);
        ExamEntity saved = examRepository.save(exam);
        return toResponse(saved);
    }

    // 🔹 Edit exam fully → return DTO
    @Transactional
    public ExamResponse editExamFull(Long examId, EditExamRequest request) {
        ExamEntity exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (exam.isPublished()) {
            throw new RuntimeException("Cannot edit published exam");
        }

        SubjectEntity subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        exam.setTitle(request.getTitle());
        exam.setSubject(subject);

        syncQuestions(exam, request.getQuestions());

        ExamEntity saved = examRepository.save(exam);
        return toResponse(saved);
    }

    // 🔹 Mapper: Entity → DTO
    private ExamResponse toResponse(ExamEntity entity) {
        return new ExamResponse(entity.getId(), entity.getTitle(), entity.isPublished());
    }

    // 🔹 Sync questions
    private void syncQuestions(ExamEntity exam, List<QuestionEditRequest> questionRequests) {
        Map<Long, QuestionEntity> existingQuestions = exam.getQuestions()
                .stream()
                .collect(Collectors.toMap(QuestionEntity::getId, q -> q));

        List<QuestionEntity> updatedQuestions = new ArrayList<>();

        for (QuestionEditRequest req : questionRequests) {
            QuestionEntity question;
            if (req.getId() != null && existingQuestions.containsKey(req.getId())) {
                question = existingQuestions.get(req.getId());
                question.setQuestionText(req.getQuestionText());
            } else {
                question = new QuestionEntity();
                question.setExam(exam);
                question.setQuestionText(req.getQuestionText());
            }
            syncOptions(question, req.getOptions());
            updatedQuestions.add(question);
        }

        exam.getQuestions().clear();
        exam.getQuestions().addAll(updatedQuestions);
    }

    // 🔹 Sync options
    private void syncOptions(QuestionEntity question, List<OptionEditRequest> optionRequests) {
        Map<Long, OptionEntity> existingOptions = question.getOptions()
                .stream()
                .collect(Collectors.toMap(OptionEntity::getId, o -> o));

        List<OptionEntity> updatedOptions = new ArrayList<>();

        for (OptionEditRequest req : optionRequests) {
            OptionEntity option;
            if (req.getId() != null && existingOptions.containsKey(req.getId())) {
                option = existingOptions.get(req.getId());
                option.setText(req.getText());
                option.setCorrect(req.isCorrect());
            } else {
                option = new OptionEntity();
                option.setQuestion(question);
                option.setText(req.getText());
                option.setCorrect(req.isCorrect());
            }
            updatedOptions.add(option);
        }

        question.getOptions().clear();
        question.getOptions().addAll(updatedOptions);
    }
}
