package com.harshbisht.WebService.controller;

import com.harshbisht.WebService.dto.AuthResponse;
import com.harshbisht.WebService.dto.LoginRequest;
import com.harshbisht.WebService.dto.RegisterRequest;
import com.harshbisht.WebService.external.AuthFeignClient;
import com.harshbisht.WebService.service.PageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
/*
Flow:
User logs in → email stored in session.
User navigates to /student/home.
Controller fetches email from session → adds it to model.
View (student/home.html) renders with the user’s email displayed.
 */
@Controller
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(LoginRequest req, HttpSession session) {

        // Call AuthService -> /auth/login
        String loginRole = pageService.login(req, session);

        return "redirect:/" + loginRole.toLowerCase() + "/home";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(RegisterRequest req) {
        pageService.register(req);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/student/home")
    public String student(HttpSession session, Model model) {
        model.addAttribute("name", session.getAttribute("email"));
        return "student/home";
    }

    @GetMapping("/teacher/home")
    public String teacher(HttpSession session, Model model) {
        model.addAttribute("name", session.getAttribute("email"));
        return "teacher/home";
    }

    @GetMapping("/admin/home")
    public String admin(HttpSession session, Model model) {
        model.addAttribute("name", session.getAttribute("email"));
        return "admin/home";
    }

    @GetMapping("/access-denied")
    public String denied() {
        return "access-denied";
    }
}