package com.harshbisht.WebService.controller.studentController;

import com.harshbisht.WebService.service.StudentService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {

	private final StudentService studentService;

	@GetMapping("/subjects")
	public String subjects(Model model) {
		model.addAttribute("subjects", studentService.getAllSubjects());
		return "student/subject";
	}

	@GetMapping("/exams/{subjectId}")
	public String exams(@PathVariable Long subjectId, Model model) {
		model.addAttribute("subjectId", subjectId);
		model.addAttribute("examViews", studentService.getExamViewsBySubject(subjectId));
		return "student/examList";
	}

	@GetMapping("/takeExam/{examId}")
	public String take(@PathVariable Long examId, Model model) {
		var existing = studentService.myResultForExam(examId);
		if (existing != null) return "redirect:/student/results/" + existing.getId();
		model.addAttribute("exam", studentService.getExamForAttempt(examId));
		return "student/takeExam";
	}

	@PostMapping("/submitExam/{examId}")
	public String submit(@PathVariable Long examId, @RequestParam Map<String, String> params) {
		Map<Long, Long> selections = new HashMap<>();
		params.forEach((k, v) -> {
			if (k.startsWith("question_")) selections.put(Long.valueOf(k.substring(9)), Long.valueOf(v));
		});
		var result = studentService.submit(examId, selections);
		return "redirect:/student/results/" + result.getId();
	}

	@GetMapping("/results")
	public String results(Model model) {
		model.addAttribute("results", studentService.myResults());
		return "student/resultHistory";
	}

	@GetMapping("/results/{id}")
	public String result(@PathVariable Long id, Model model) {
		model.addAttribute("result", studentService.getResult(id));
		return "student/examResult";
	}
}
