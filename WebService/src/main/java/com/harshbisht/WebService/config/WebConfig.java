package com.harshbisht.WebService.config;

import com.harshbisht.WebService.exception.RoleMismatchException;
import com.harshbisht.WebService.exception.SessionExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
/*
Flow:
User logs in → session is created, with attributes:
    token → JWT or session token.
    role → STUDENT / TEACHER / ADMIN.

User navigates to /student/home.
    Interceptor checks session → valid.
    Role = STUDENT → allowed.

If a TEACHER tries /student/home → redirected to /access-denied.

If session expired → redirected to /login.
 */
// This is where you enforce session-based role checks for your dashboards
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Registers your custom AuthInterceptor.
    // Applies it to all paths under /student/**, /teacher/**, /admin/**.
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/student/**", "/teacher/**", "/admin/**");
    }

    // Inner Class
    // Implements HandlerInterceptor → runs before controller methods.
    static class AuthInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest req,
                                 HttpServletResponse res,
                                 Object handler) throws Exception {

            HttpSession session = req.getSession(false);

            // Custom Exceptions
            // Getting Handled at GlobalExceptionHandler

            // session expired -> back to login page
            if (session == null || session.getAttribute("token") == null) {
                throw new SessionExpiredException("Session expired or not logged in");
            }

            String role = (String) session.getAttribute("role");
            String uri = req.getRequestURI();

            // session role and URI role should be same
            if (uri.startsWith("/student") && !role.equals("STUDENT")) {
                throw new RoleMismatchException("Only STUDENT can access this page");
            }

            if (uri.startsWith("/teacher") && !role.equals("TEACHER")) {
                throw new RoleMismatchException("Only TEACHER can access this page");
            }

            if (uri.startsWith("/admin") && !role.equals("ADMIN")) {
                throw new RoleMismatchException("Only ADMIN can access this page");
            }

            return true;
        }

    }
}