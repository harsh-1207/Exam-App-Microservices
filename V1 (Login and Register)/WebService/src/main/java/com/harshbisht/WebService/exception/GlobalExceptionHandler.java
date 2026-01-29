package com.harshbisht.WebService.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SessionExpiredException.class)
    public String handleSessionExpired(SessionExpiredException ex) {
        return "redirect:/login"; // redirect to login page
    }

    @ExceptionHandler(RoleMismatchException.class)
    public String handleRoleMismatch(RoleMismatchException ex) {
        return "redirect:/access-denied"; // redirect to access denied page
    }

    @ExceptionHandler(PageAccessDeniedException.class)
    public String handlePageAccessDenied(PageAccessDeniedException ex) {
        return "access-denied"; // show access denied view
    }

    @ExceptionHandler(InvalidSessionTokenException.class)
    public String handleInvalidToken(InvalidSessionTokenException ex) {
        return "redirect:/login"; // force re-login
    }
}

