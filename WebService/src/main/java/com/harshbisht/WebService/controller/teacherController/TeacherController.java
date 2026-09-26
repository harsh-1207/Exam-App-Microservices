package com.harshbisht.WebService.controller.teacherController;

import com.harshbisht.WebService.external.dto.ExamDTO.EditExamRequest;
import com.harshbisht.WebService.external.dto.ExamDTO.ExamResponse;
import com.harshbisht.WebService.external.dto.OptionDTO.OptionEditRequest;
import com.harshbisht.WebService.external.dto.OptionDTO.OptionRequest;
import com.harshbisht.WebService.external.dto.QuestionDTO.AddQuestionRequest;
import com.harshbisht.WebService.external.dto.QuestionDTO.QuestionEditRequest;
import com.harshbisht.WebService.service.TeacherService;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

	private static final String DRAFT_QUESTIONS = "draftQuestions";
	private final TeacherService teacherService;

	@GetMapping("/subjects")
	public String getSubjects(Model model) {
		model.addAttribute("subjects", teacherService.getAllSubjects());
		return "teacher/subject";
	}

	@PostMapping("/subjects")
	public String createSubject(@RequestParam String name) {
		teacherService.createSubject(name);
		return "redirect:/teacher/subjects";
	}

	@GetMapping("/subjects/{subjectId}/exams")
	public String getExamsBySubject(@PathVariable Long subjectId, Model model) {
		model.addAttribute("subject", teacherService.getSubject(subjectId));
		model.addAttribute("exams", teacherService.getExamsBySubject(subjectId));
		return "teacher/examList";
	}

	@GetMapping("/createExam")
	public String createExamPage(@RequestParam Long subjectId, Model model) {
		model.addAttribute("subjectId", subjectId);
		return "teacher/createExam";
	}

	@PostMapping("/createExam")
	public String createExam(@RequestParam Long subjectId, @RequestParam String title, HttpSession session) {
		ExamResponse exam = teacherService.createExam(subjectId, title);
		session.setAttribute("currentExamId", exam.getId());
		session.setAttribute("currentSubjectId", subjectId);
		session.removeAttribute(DRAFT_QUESTIONS);
		return "redirect:/teacher/addQuestions";
	}

	@GetMapping("/addQuestions")
	public String addQuestionsPage(HttpSession session, Model model) {
		Long examId = (Long) session.getAttribute("currentExamId");
		if (examId == null) return "redirect:/teacher/subjects";
		List<AddQuestionRequest> drafts = draftQuestions(session);
		model.addAttribute("draftQuestions", drafts);
		model.addAttribute("examId", examId);
		return "teacher/addQuestions";
	}

	// Adds only to the HTTP session. Nothing is persisted until /submitQuestions.
	@PostMapping("/addQuestion")
	public String addQuestion(
		@RequestParam String questionText,
		@RequestParam List<String> optionText,
		@RequestParam int correctIndex,
		HttpSession session
	) {
		Long examId = (Long) session.getAttribute("currentExamId");
		if (examId == null) return "redirect:/teacher/subjects";
		List<OptionRequest> options = new ArrayList<>();
		for (int i = 0; i < optionText.size(); i++) options.add(
			new OptionRequest(null, optionText.get(i), i == correctIndex)
		);
		draftQuestions(session)
			.add(AddQuestionRequest.builder().examId(examId).questionText(questionText).options(options).build());
		return "redirect:/teacher/addQuestions";
	}

	@PostMapping("/submitQuestions")
	public String submitQuestions(HttpSession session) {
		Long examId = (Long) session.getAttribute("currentExamId");
		Long subjectId = (Long) session.getAttribute("currentSubjectId");
		if (examId == null) return "redirect:/teacher/subjects";
		List<AddQuestionRequest> drafts = draftQuestions(session);
		if (drafts.isEmpty()) {
			return "redirect:/teacher/addQuestions";
		}
		teacherService.addQuestions(examId, drafts);
		clearDraft(session);
		return subjectId == null ? "redirect:/teacher/subjects" : "redirect:/teacher/subjects/" + subjectId + "/exams";
	}

	@PostMapping("/cancelQuestions")
	public String cancelQuestions(HttpSession session) {
		Long examId = (Long) session.getAttribute("currentExamId");
		Long subjectId = (Long) session.getAttribute("currentSubjectId");
		clearDraft(session);
		// A cancelled create flow must not leave an empty exam behind.
		if (examId != null) teacherService.deleteExam(examId);
		return subjectId == null ? "redirect:/teacher/subjects" : "redirect:/teacher/subjects/" + subjectId + "/exams";
	}

	@SuppressWarnings("unchecked")
	private List<AddQuestionRequest> draftQuestions(HttpSession session) {
		List<AddQuestionRequest> drafts = (List<AddQuestionRequest>) session.getAttribute(DRAFT_QUESTIONS);
		if (drafts == null) {
			drafts = new ArrayList<>();
			session.setAttribute(DRAFT_QUESTIONS, drafts);
		}
		return drafts;
	}

	private void clearDraft(HttpSession session) {
		session.removeAttribute(DRAFT_QUESTIONS);
		session.removeAttribute("currentExamId");
		session.removeAttribute("currentSubjectId");
	}

	@GetMapping("/editExam/{examId}")
	public String editExam(@PathVariable Long examId, Model model) {
		model.addAttribute("exam", teacherService.getExamWithQuestions(examId));
		return "teacher/editExam";
	}

	@PostMapping("/editExam/{examId}")
	public String saveExam(
		@PathVariable Long examId,
		@RequestParam String title,
		@RequestParam Long subjectId,
		@RequestParam(defaultValue = "false") boolean published,
		@RequestParam(required = false) List<String> questionId,
		@RequestParam(required = false) List<String> questionText,
		@RequestParam(required = false) List<Integer> correctIndex,
		@RequestParam(required = false) List<String> optionIdFlat,
		@RequestParam(required = false) List<String> optionTextFlat
	) {
		EditExamRequest request = new EditExamRequest();
		request.setTitle(title);
		request.setSubjectId(subjectId);
		request.setPublished(published);
		List<QuestionEditRequest> questions = new ArrayList<>();
		if (questionText != null) {
			for (int i = 0; i < questionText.size(); i++) {
				QuestionEditRequest q = new QuestionEditRequest();
				if (questionId != null && i < questionId.size()) {
					try {
						long id = Long.parseLong(questionId.get(i));
						if (id > 0) q.setId(id);
					} catch (Exception ignored) {}
				}
				q.setQuestionText(questionText.get(i));
				q.setMarks(1);
				List<OptionEditRequest> opts = new ArrayList<>();
				for (int j = 0; j < 4; j++) {
					int flat = i * 4 + j;
					OptionEditRequest o = new OptionEditRequest();
					if (optionIdFlat != null && flat < optionIdFlat.size()) {
						try {
							long id = Long.parseLong(optionIdFlat.get(flat));
							if (id > 0) o.setId(id);
						} catch (Exception ignored) {}
					}
					o.setText(optionTextFlat.get(flat));
					o.setCorrect(correctIndex != null && i < correctIndex.size() && correctIndex.get(i) == j);
					opts.add(o);
				}
				q.setOptions(opts);
				questions.add(q);
			}
		}
		request.setQuestions(questions);
		teacherService.editExam(examId, request);
		return "redirect:/teacher/subjects/" + subjectId + "/exams";
	}

	@GetMapping("/exams/{examId}/questions/{questionId}/edit")
	public String editQuestionPage(@PathVariable Long examId, @PathVariable Long questionId, Model model) {
		model.addAttribute("question", teacherService.getQuestion(examId, questionId));
		model.addAttribute("examId", examId);
		return "teacher/editQuestion";
	}

	@PostMapping("/exams/{examId}/questions/{questionId}/edit")
	public String editQuestion(
		@PathVariable Long examId,
		@PathVariable Long questionId,
		@RequestParam String questionText,
		@RequestParam List<String> optionText,
		@RequestParam int correctIndex
	) {
		List<OptionRequest> options = new ArrayList<>();
		for (int i = 0; i < optionText.size(); i++) options.add(
			new OptionRequest(null, optionText.get(i), i == correctIndex)
		);
		AddQuestionRequest request = AddQuestionRequest
			.builder()
			.examId(examId)
			.questionText(questionText)
			.options(options)
			.build();
		teacherService.updateQuestion(examId, questionId, request);
		return "redirect:/teacher/editExam/" + examId;
	}

	@PostMapping("/exams/{examId}/status")
	public String changeExamStatus(
		@PathVariable Long examId,
		@RequestParam boolean published,
		@RequestParam Long subjectId
	) {
		if (published) {
			teacherService.publishExam(examId);
		} else {
			teacherService.unpublishExam(examId);
		}
		return "redirect:/teacher/subjects/" + subjectId + "/exams";
	}

	@GetMapping("/reports")
	public String reports(Model model) {
		model.addAttribute("examReports", teacherService.getAllExamReports());
		return "teacher/reports";
	}

	@GetMapping("/exams/{examId}/results")
	public String examResults(@PathVariable Long examId, Model model) {
		model.addAttribute("exam", teacherService.getExamWithQuestions(examId));
		model.addAttribute("resultViews", teacherService.getExamResultViews(examId));
		return "teacher/examResults";
	}
}
