package com.harshbisht.WebService.controller.studentController;

import com.harshbisht.WebService.service.StudentService;
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
    public String getSubjects(Model model) {
        model.addAttribute("subjects", studentService.getAllSubjects());
        return "student/subject";
    }

    @GetMapping("/exams/{subjectId}")
    public String getExamsBySubject(@PathVariable Long subjectId, Model model) {
        model.addAttribute("subjectId", subjectId);
        model.addAttribute("exams", studentService.getExamsBySubject(subjectId));
        return "student/examList";
    }

    @GetMapping("/takeExam/{examId}")
    public String takeExam(@PathVariable Long examId, Model model) {
        model.addAttribute("exam", studentService.getExamWithQuestions(examId));
        return "student/takeExam";
    }
}
