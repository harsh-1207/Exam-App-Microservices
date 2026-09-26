package com.harshbisht.WebService.service;

import com.harshbisht.WebService.dto.StudentExamView;
import com.harshbisht.WebService.external.ExamFeignClient;
import com.harshbisht.WebService.external.ResultFeignClient;
import com.harshbisht.WebService.external.UserFeignClient;
import com.harshbisht.WebService.external.dto.AttemptDTO.ExamAttemptResponse;
import com.harshbisht.WebService.external.dto.ExamDTO.ExamResponse;
import com.harshbisht.WebService.external.dto.ResultDTO.ResultResponse;
import com.harshbisht.WebService.external.dto.ResultDTO.SubmitExamRequest;
import com.harshbisht.WebService.external.dto.SubjectDTO.SubjectResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

	private final ExamFeignClient examFeign;
	private final ResultFeignClient resultFeign;
	private final UserFeignClient userFeign;

	public List<SubjectResponse> getAllSubjects() {
		return examFeign.getAllSubjects();
	}

	public List<StudentExamView> getExamViewsBySubject(Long subjectId) {
		List<ExamResponse> exams = examFeign.getExamsBySubject(subjectId);
		if (exams == null) exams = List.of();
		Map<Long, ResultResponse> results = myResults()
			.stream()
			.collect(Collectors.toMap(ResultResponse::getExamId, r -> r, (a, b) -> a));
		Map<Long, String> teacherNames = new HashMap<>();
		return exams
			.stream()
			.map(exam -> {
				String teacherName = teacherNames.computeIfAbsent(exam.getTeacherId(), this::resolveUserName);
				return new StudentExamView(exam, teacherName, results.get(exam.getId()));
			})
			.toList();
	}

	private String resolveUserName(Long userId) {
		if (userId == null) return "Unknown teacher";
		try {
			var user = userFeign.getUserSummary(userId);
			return user != null && user.getName() != null && !user.getName().isBlank()
				? user.getName()
				: "Teacher #" + userId;
		} catch (Exception ignored) {
			return "Teacher #" + userId;
		}
	}

	public ExamAttemptResponse getExamForAttempt(Long examId) {
		return examFeign.getExamForAttempt(examId);
	}

	public ResultResponse submit(Long examId, Map<Long, Long> selections) {
		var answers = selections
			.entrySet()
			.stream()
			.map(e -> new SubmitExamRequest.AnswerRequest(e.getKey(), e.getValue()))
			.toList();
		return resultFeign.submit(new SubmitExamRequest(examId, answers));
	}

	public List<ResultResponse> myResults() {
		List<ResultResponse> results = resultFeign.myResults();
		return results != null ? results : List.of();
	}

	public ResultResponse myResultForExam(Long examId) {
		return resultFeign.myResultForExam(examId);
	}

	public ResultResponse getResult(Long id) {
		return resultFeign.getResult(id);
	}
}
