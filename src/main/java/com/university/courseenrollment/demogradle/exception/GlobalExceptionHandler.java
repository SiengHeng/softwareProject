package com.university.courseenrollment.demogradle.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, RedirectAttributes redirectAttributes) {
        log.error("Resource not found: ", ex);
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/dashboard";
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResource(DuplicateResourceException ex, RedirectAttributes redirectAttributes) {
        log.error("Duplicate resource: ", ex);
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/dashboard";
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public String handleScheduleConflict(ScheduleConflictException ex, RedirectAttributes redirectAttributes) {
        log.error("Schedule conflict: ", ex);
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/schedule/list";
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public String handleUnauthorizedAccess(UnauthorizedAccessException ex, Model model) {
        log.error("Unauthorized access: ", ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/403";
    }

    // Handle Spring Security authorization exceptions
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(Exception ex, Model model) {
        log.warn("Access denied: ", ex);
        model.addAttribute("errorMessage", "You don't have permission to access this resource.");
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        // Don't catch security exceptions here - they should be handled by the specific handler above
        if (ex instanceof AccessDeniedException || ex instanceof AuthorizationDeniedException) {
            throw (RuntimeException) ex;
        }
        
        log.error("Unexpected error occurred: ", ex);
        model.addAttribute("errorMessage", "An unexpected error occurred: " + ex.getMessage());
        return "error/500";
    }
}