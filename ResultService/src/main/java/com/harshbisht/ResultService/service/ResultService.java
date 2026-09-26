package com.harshbisht.ResultService.service;

import com.harshbisht.ResultService.dto.*;
import com.harshbisht.ResultService.entity.*;
import com.harshbisht.ResultService.exception.ResultException;
import com.harshbisht.ResultService.external.ExamFeignClient;
import com.harshbisht.ResultService.repository.ResultRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResultService {

	private final ResultRepository resultRepository;
	private final ExamFeignClient examFeignClient;

	private Long currentUserId() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		try {
			return Long.valueOf(principal.toString());
		} catch (Exception e) {
			throw new ResultException("Unable to resolve current user");
		}
	}

	private boolean hasRole(String role) {
		return SecurityContextHolder
			.getContext()
			.getAuthentication()
			.getAuthorities()
			.stream()
			.anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
	}

	@Transactional
	public ResultResponse submit(SubmitExamRequest request) {
		Long studentId = currentUserId();
		if (!hasRole("STUDENT")) {
			throw new ResultException("Only students can submit exams");
		}
		Optional<ResultEntity> existingResult = resultRepository.findByExamIdAndStudentId(
			request.getExamId(),
			studentId
		);
		if (existingResult.isPresent()) {
			return toResponse(existingResult.get());
		}
		ExamEvaluationResponse exam = examFeignClient.getEvaluationData(request.getExamId());
		if (!exam.isPublished()) throw new ResultException("This exam is not currently published");
		Map<Long, ExamEvaluationResponse.QuestionEvaluation> questions = exam
			.getQuestions()
			.stream()
			.collect(Collectors.toMap(ExamEvaluationResponse.QuestionEvaluation::getQuestionId, Function.identity()));
		Map<Long, SubmitExamRequest.AnswerRequest> submitted = request
			.getAnswers()
			.stream()
			.collect(
				Collectors.toMap(SubmitExamRequest.AnswerRequest::getQuestionId, Function.identity(), (a, b) -> a)
			);
		if (!questions.keySet().equals(submitted.keySet())) throw new ResultException(
			"Every exam question must have exactly one submitted answer"
		);
		ResultEntity result = ResultEntity
			.builder()
			.examId(exam.getExamId())
			.examTitle(exam.getTitle())
			.studentId(studentId)
			.totalMarks(exam.getTotalMarks())
			.score(0L)
			.percentage(0D)
			.passed(false)
			.submittedAt(LocalDateTime.now())
			.build();
		List<AnswerEntity> answers = new ArrayList<>();
		long score = 0;
		for (var q : exam.getQuestions()) {
			var answer = submitted.get(q.getQuestionId());
			boolean correct = q.getCorrectOptionId().equals(answer.getSelectedOptionId());
			if (correct) score += q.getMarks();
			answers.add(
				AnswerEntity
					.builder()
					.questionId(q.getQuestionId())
					.selectedOptionId(answer.getSelectedOptionId())
					.correct(correct)
					.result(result)
					.build()
			);
		}
		double percentage = exam.getTotalMarks() == 0 ? 0 : (score * 100.0 / exam.getTotalMarks());
		result.setScore(score);
		result.setPercentage(percentage);
		result.setPassed(percentage >= exam.getPassingPercentage());
		result.setAnswers(answers);
		return toResponse(resultRepository.save(result));
	}

	public ResultResponse myResultForExam(Long examId) {
		return resultRepository.findByExamIdAndStudentId(examId, currentUserId()).map(this::toResponse).orElse(null);
	}

	public List<ResultResponse> myResults() {
		return resultRepository
			.findByStudentIdOrderBySubmittedAtDesc(currentUserId())
			.stream()
			.map(this::toResponse)
			.toList();
	}

	public ResultResponse getResult(Long id) {
		ResultEntity r = resultRepository.findById(id).orElseThrow(() -> new ResultException("Result not found"));
		if (
			!hasRole("TEACHER") && !hasRole("ADMIN") && !r.getStudentId().equals(currentUserId())
		) throw new ResultException("You cannot view this result");
		return toResponse(r);
	}

	public List<ResultResponse> examResults(Long examId) {
		ExamEvaluationResponse exam = examFeignClient.getEvaluationData(examId);
		if (hasRole("TEACHER") && !exam.getTeacherId().equals(currentUserId())) throw new ResultException(
			"You do not own this exam"
		);
		return resultRepository.findByExamIdOrderBySubmittedAtDesc(examId).stream().map(this::toResponse).toList();
	}

	private ResultResponse toResponse(ResultEntity r) {
		return ResultResponse
			.builder()
			.id(r.getId())
			.examId(r.getExamId())
			.examTitle(r.getExamTitle())
			.studentId(r.getStudentId())
			.totalMarks(r.getTotalMarks())
			.score(r.getScore())
			.percentage(r.getPercentage())
			.passed(Boolean.TRUE.equals(r.getPassed()))
			.submittedAt(r.getSubmittedAt())
			.answers(
				r.getAnswers() == null
					? List.of()
					: r
						.getAnswers()
						.stream()
						.map(a ->
							ResultResponse.AnswerResult
								.builder()
								.questionId(a.getQuestionId())
								.selectedOptionId(a.getSelectedOptionId())
								.correct(Boolean.TRUE.equals(a.getCorrect()))
								.build()
						)
						.toList()
			)
			.build();
	}
}
