package com.harshbisht.WebService.controller;

import com.harshbisht.WebService.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class TeacherController {

    private final PageService pageService;

    @GetMapping("/teacher")
    public String teacherHome() {
        return "teacher";
    }

    @GetMapping("/teacher/subjects")
    public String getSubjects(Model model) {
        // model.addAttribute("subjects", subjectService.getAllSubjects());
        // TODO: Move this logic to pageService and make a feign call to the ExamService
        return "teacher/subjects";
    }
}
