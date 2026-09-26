package com.harshbisht.WebService.service;

import com.harshbisht.WebService.dto.TeacherExamReportView;
import com.harshbisht.WebService.dto.TeacherResultView;
import com.harshbisht.WebService.external.ExamFeignClient;
import com.harshbisht.WebService.external.UserFeignClient;
import com.harshbisht.WebService.external.dto.ExamDTO.CreateExamRequest;
import com.harshbisht.WebService.external.dto.ExamDTO.ExamDetailResponse;
import com.harshbisht.WebService.external.dto.ExamDTO.ExamResponse;
import com.harshbisht.WebService.external.dto.OptionDTO.OptionRequest;
import com.harshbisht.WebService.external.dto.QuestionDTO.AddQuestionRequest;
import com.harshbisht.WebService.external.dto.SubjectDTO.CreateSubjectRequest;
import com.harshbisht.WebService.external.dto.SubjectDTO.SubjectResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

	private final ExamFeignClient examFeign;
	private final com.harshbisht.WebService.external.ResultFeignClient resultFeignClient;
	private final UserFeignClient userFeignClient;

	public List<SubjectResponse> getAllSubjects() {
		return examFeign.getAllSubjects();
	}

	public SubjectResponse createSubject(String name) {
		// Build the DTO here
		CreateSubjectRequest request = new CreateSubjectRequest();
		request.setName(name);

		// Delegate to Feign client
		return examFeign.createSubject(request);
	}

	public SubjectResponse getSubject(Long subjectId) {
		SubjectResponse subject = examFeign.getSubject(subjectId);

		if (subject == null) {
			throw new RuntimeException("Subject not found with id: " + subjectId);
		}

		return subject;
	}

	public List<ExamResponse> getExamsBySubject(Long subjectId) {
		List<ExamResponse> exams = examFeign.getExamsBySubject(subjectId);
		return exams != null ? exams : List.of();
	}

	public ExamDetailResponse getExamWithQuestions(Long examId) {
		return examFeign.getExamWithQuestions(examId);
	}

	public List<ExamResponse> getMyExams() {
		List<ExamResponse> exams = examFeign.getMyExams();
		return exams != null ? exams : List.of();
	}

	public ExamResponse editExam(Long examId, com.harshbisht.WebService.external.dto.ExamDTO.EditExamRequest request) {
		return examFeign.editFullExam(examId, request).getBody();
	}

	public ExamResponse createExam(Long subjectId, String title) {
		CreateExamRequest request = new CreateExamRequest(title, subjectId);

		return examFeign.createExam(request).getBody();
	}

	public void addQuestion(Long examId, String questionText, List<OptionRequest> options) {
		AddQuestionRequest request = AddQuestionRequest
			.builder()
			.examId(examId)
			.questionText(questionText)
			.options(options)
			.build();

		examFeign.addQuestion(examId, request);
	}

	public void addQuestions(Long examId, List<AddQuestionRequest> questions) {
		if (questions == null) return;
		for (AddQuestionRequest request : questions) {
			request.setExamId(examId);
			examFeign.addQuestion(examId, request);
		}
	}

	public void deleteExam(Long examId) {
		examFeign.deleteExam(examId);
	}

	public ExamResponse publishExam(Long examId) {
		return examFeign.publishExam(examId).getBody();
	}

	public ExamResponse unpublishExam(Long examId) {
		return examFeign.unpublishExam(examId).getBody();
	}

	public com.harshbisht.WebService.external.dto.QuestionDTO.QuestionResponse getQuestion(
		Long examId,
		Long questionId
	) {
		return examFeign.getQuestion(examId, questionId);
	}

	public com.harshbisht.WebService.external.dto.QuestionDTO.QuestionResponse updateQuestion(
		Long examId,
		Long questionId,
		AddQuestionRequest request
	) {
		return examFeign.updateQuestion(examId, questionId, request);
	}

	public java.util.List<com.harshbisht.WebService.external.dto.ResultDTO.ResultResponse> getExamResults(Long examId) {
		var results = resultFeignClient.examResults(examId);
		return results != null ? results : java.util.List.of();
	}

	public java.util.List<TeacherResultView> getExamResultViews(Long examId) {
		return getExamResults(examId)
			.stream()
			.map(result -> new TeacherResultView(result, resolveUserName(result.getStudentId())))
			.toList();
	}

	public java.util.List<TeacherExamReportView> getAllExamReports() {
		return getMyExams()
			.stream()
			.map(exam -> new TeacherExamReportView(exam, getExamResultViews(exam.getId())))
			.toList();
	}

	private String resolveUserName(Long userId) {
		if (userId == null) return "Unknown student";
		try {
			var user = userFeignClient.getUserSummary(userId);
			return user != null && user.getName() != null && !user.getName().isBlank()
				? user.getName()
				: "Student #" + userId;
		} catch (Exception ignored) {
			return "Student #" + userId;
		}
	}
}
