package com.harshbisht.WebService.controller.teacherController;

import com.harshbisht.WebService.external.dto.ExamDTO.ExamResponse;
import com.harshbisht.WebService.external.dto.OptionDTO.OptionRequest;
import com.harshbisht.WebService.service.TeacherService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// APIs will be added later to show data on the home page
@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    // ***********************************************************  /teacher/home ***************************************************************

    // Manage Subjects:
    // ***********************************************************  /teacher/subjects ***************************************************************
    // get all subjects :
    @GetMapping("/subjects")
    public String getSubjects(Model model) {
        model.addAttribute("subjects", teacherService.getAllSubjects());
        return "teacher/subject";
    }
    // Create Subject
    @PostMapping("/subjects")
    public String createSubject(@RequestParam String name) {
        teacherService.createSubject(name);
        return "redirect:/teacher/subjects";
    }

    // View Exams:
    // ***********************************************************  /teacher/subjects/{id}/exams ***************************************************************
    // Read Exam by SubjectId
    @GetMapping("/subjects/{subjectId}/exams")
    public String getExamsBySubject(@PathVariable Long subjectId, Model model) {
        model.addAttribute("subject", teacherService.getSubject(subjectId));
        model.addAttribute("exams", teacherService.getExamsBySubject(subjectId));
        return "teacher/examList";
    }

    // Add Exam:
    // ***********************************************************  /teacher/createExam ***************************************************************
    // Create Exam
    @GetMapping("/createExam")
    public String createExamPage(@RequestParam Long subjectId, Model model) {
        model.addAttribute("subjectId", subjectId);
        return "teacher/createExam";
    }

//    @PostMapping("/createExam")
//    public String createExam(@RequestParam Long subjectId,
//                             @RequestParam String title) {
//
//        teacherService.createExam(subjectId, title);
//
//        return "redirect:/teacher/subjects/" + subjectId + "/exams";
//    }
    @PostMapping("/createExam")
    public String createExam(@RequestParam Long subjectId,
                             @RequestParam String title,
                             HttpSession session) {

        ExamResponse exam = teacherService.createExam(subjectId, title);

        session.setAttribute("currentExamId", exam.getId());

        return "redirect:/teacher/addQuestions";
    }

    @GetMapping("/addQuestions")
    public String addQuestionsPage() {
        return "teacher/addQuestions";
    }

    @PostMapping("/addQuestion")
    public String addQuestion(@RequestParam String questionText,
                              @RequestParam List<String> optionText,
                              @RequestParam int correctIndex,
                              HttpSession session) {

        Long examId = (Long) session.getAttribute("currentExamId");

        List<OptionRequest> options = new ArrayList<>();

        for (int i = 0; i < optionText.size(); i++) {
            options.add(new OptionRequest(
                    null,
                    optionText.get(i),
                    i == correctIndex
            ));
        }

        teacherService.addQuestion(examId, questionText, options);

        return "redirect:/teacher/addQuestions";
    }

    // Update Exam
    @GetMapping("/editExam/{examId}")
    public String editExam(@PathVariable Long examId, Model model) {
        model.addAttribute("exam", teacherService.getExamWithQuestions(examId));
        return "teacher/editExam";
    }


}