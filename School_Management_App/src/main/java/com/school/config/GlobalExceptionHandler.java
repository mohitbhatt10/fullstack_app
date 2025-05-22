package com.school.config;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model, HttpServletRequest request) {
        logException(ex, request);
        String userMessage = ex.getMessage();
        // If the message is null or too technical, provide a user-friendly message
        if (userMessage == null || userMessage.contains("StackOverflow")) {
            userMessage = "An error occurred while processing your request. Please try again.";
        }
        model.addAttribute("errorMessage", userMessage);
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model, HttpServletRequest request) {
        logSecurityException(ex, request);
        model.addAttribute("errorMessage", "You don't have permission to access this resource.");
        return "error";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleValidationException(ConstraintViolationException ex, Model model, HttpServletRequest request) {
        logValidationException(ex, request);
        model.addAttribute("errorMessage", "Validation error: " + ex.getMessage());
        return "error";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolationException(DataIntegrityViolationException ex, 
                                                       RedirectAttributes redirectAttributes,
                                                       HttpServletRequest request) {
        String errorMessage;
        if (ex.getMessage().contains("UK_username")) {
            errorMessage = "Username already exists.";
            logDatabaseException("Duplicate username attempt", ex, request);
        } else if (ex.getMessage().contains("UK_email")) {
            errorMessage = "Email already exists.";
            logDatabaseException("Duplicate email attempt", ex, request);
        } else if (ex.getMessage().contains("UK_roll_number")) {
            errorMessage = "Roll number already exists.";
            logDatabaseException("Duplicate roll number attempt", ex, request);
        } else {
            errorMessage = "Database error occurred. Please try again.";
            logDatabaseException("Unexpected database error", ex, request);
        }
        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:/error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        // Only log favicon.ico errors at debug level
        if (request.getRequestURI().endsWith("favicon.ico")) {
            logger.debug("Favicon request: {}", ex.getMessage());
        } else {
            logger.warn("Resource not found: {}", ex.getMessage());
        }
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
        logException(ex, request);
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        return "error";
    }

    private void logException(Exception ex, HttpServletRequest request) {
        Map<String, String> requestDetails = getRequestDetails(request);
        
        logger.error("\nException Details:" +
                    "\nTimestamp: {}" +
                    "\nPath: {}" +
                    "\nMethod: {}" +
                    "\nRequest Parameters: {}" +
                    "\nRequest Headers: {}" +
                    "\nError: {}" +
                    "\nMessage: {}" +
                    "\nRoot Cause: {}" +
                    "\nStack trace: {}",
                    new java.util.Date(),
                    request.getRequestURI(),
                    request.getMethod(),
                    requestDetails.get("parameters"),
                    requestDetails.get("headers"),
                    ex.getClass().getCanonicalName(),
                    ex.getMessage(),
                    getRootCause(ex),
                    Arrays.toString(ex.getStackTrace())
        );
    }

    private Map<String, String> getRequestDetails(HttpServletRequest request) {
        Map<String, String> details = new HashMap<>();
        
        // Get request parameters
        StringBuilder params = new StringBuilder();
        request.getParameterMap().forEach((key, value) -> {
            params.append(key).append("=").append(Arrays.toString(value)).append(", ");
        });
        
        // Get request headers
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!headerName.toLowerCase().contains("cookie")) { // Skip sensitive headers
                headers.append(headerName).append("=").append(request.getHeader(headerName)).append(", ");
            }
        }
        
        details.put("parameters", params.toString());
        details.put("headers", headers.toString());
        return details;
    }

    private void logSecurityException(AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("\nSecurity Exception:" +
                   "\nTimestamp: {}" +
                   "\nPath: {}" +
                   "\nUser: {}" +
                   "\nMessage: {}",
                   new java.util.Date(),
                   request.getRequestURI(),
                   request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous",
                   ex.getMessage()
        );
    }

    private void logValidationException(ConstraintViolationException ex, HttpServletRequest request) {
        logger.warn("\nValidation Exception:" +
                   "\nTimestamp: {}" +
                   "\nPath: {}" +
                   "\nViolations: {}" +
                   "\nMessage: {}",
                   new java.util.Date(),
                   request.getRequestURI(),
                   ex.getConstraintViolations(),
                   ex.getMessage()
        );
    }

    private void logDatabaseException(String context, DataIntegrityViolationException ex, HttpServletRequest request) {
        logger.error("\nDatabase Exception:" +
                    "\nTimestamp: {}" +
                    "\nPath: {}" +
                    "\nContext: {}" +
                    "\nError: {}" +
                    "\nRoot Cause: {}" +
                    "\nStack trace: {}",
                    new java.util.Date(),
                    request.getRequestURI(),
                    context,
                    ex.getMessage(),
                    getRootCause(ex),
                    Arrays.toString(ex.getStackTrace())
        );
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}
