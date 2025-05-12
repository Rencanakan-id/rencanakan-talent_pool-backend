package rencanakan.id.talentpool.controller;

import io.opentracing.Span;
import io.opentracing.Tracer;
import datadog.trace.api.GlobalTracer;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import rencanakan.id.talentpool.dto.WebResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);
    private final Tracer tracer;

    @Autowired
    public ErrorController(Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<String>> handleException(Exception ex, WebRequest request) {

        Span span = tracer.activeSpan();

        if (span != null) {
            // Tag the span with error information
            span.setTag("error", true);
            span.setTag("error.msg", ex.getMessage());
            span.setTag("error.type", ex.getClass().getName());
            span.setTag("error.category", "server");
            span.setTag("error.severity", "critical");

            // Add request information
            span.setTag("request.path", request.getDescription(false));

            // Log the error with context
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", ex.getMessage());
            errorDetails.put("path", request.getDescription(false));
            errorDetails.put("stacktrace", getStackTraceAsString(ex));
            span.log(errorDetails);
        }

        // Log the error
        logger.error("Unhandled exception: ", ex);

        // Return error response in consistent format
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(WebResponse.<String>builder()
                        .errors("An unexpected error occurred: " + ex.getMessage())
                        .build());
    }

    /**
     * Handle entity not found exceptions
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<WebResponse<String>> handleEntityNotFoundException(
            EntityNotFoundException ex, WebRequest request) {

        Span span = tracer.activeSpan();

        if (span != null) {
            span.setTag("error", true);
            span.setTag("error.msg", ex.getMessage());
            span.setTag("error.type", "EntityNotFound");
            span.setTag("error.category", "business");
            span.setTag("error.severity", "warning");
            span.setTag("request.path", request.getDescription(false));

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", ex.getMessage());
            errorDetails.put("path", request.getDescription(false));
            span.log(errorDetails);
        }

        logger.warn("Entity not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(WebResponse.<String>builder()
                        .errors(ex.getMessage())
                        .build());
    }

    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<String>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));


        Span span = tracer.activeSpan();

        if (span != null) {
            span.setTag("error", true);
            span.setTag("error.msg", errors);
            span.setTag("error.type", "ValidationError");
            span.setTag("error.category", "validation");
            span.setTag("error.severity", "warning");
            span.setTag("request.path", request.getDescription(false));

            // Add validation details
            ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
                span.setTag("validation.field." + fieldError.getField(),
                        fieldError.getDefaultMessage());
            });

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", errors);
            errorDetails.put("path", request.getDescription(false));
            span.log(errorDetails);
        }

        logger.warn("Validation error: {}", errors);

        return ResponseEntity.badRequest()
                .body(WebResponse.<String>builder()
                        .errors(errors)
                        .build());
    }

    /**
     * Handle missing header exceptions
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<WebResponse<String>> handleMissingHeader(
            MissingRequestHeaderException ex, WebRequest request) {

        Span span = tracer.activeSpan();

        if (span != null) {
            span.setTag("error", true);
            span.setTag("error.msg", "Missing required header: " + ex.getHeaderName());
            span.setTag("error.type", "MissingHeader");
            span.setTag("error.category", "authentication");
            span.setTag("error.severity", "warning");
            span.setTag("request.path", request.getDescription(false));
            span.setTag("header.missing", ex.getHeaderName());

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Missing required header: " + ex.getHeaderName());
            errorDetails.put("path", request.getDescription(false));
            span.log(errorDetails);
        }

        logger.warn("Missing header: {}", ex.getHeaderName());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(WebResponse.<String>builder()
                        .errors("Unauthorized - Missing required header: " + ex.getHeaderName())
                        .build());
    }

    /**
     * Handle access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<WebResponse<String>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        Span span = tracer.activeSpan();

        if (span != null) {
            span.setTag("error", true);
            span.setTag("error.msg", ex.getMessage());
            span.setTag("error.type", "AccessDenied");
            span.setTag("error.category", "authorization");
            span.setTag("error.severity", "warning");
            span.setTag("request.path", request.getDescription(false));

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", ex.getMessage());
            errorDetails.put("path", request.getDescription(false));
            span.log(errorDetails);
        }

        logger.warn("Access denied: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(WebResponse.<String>builder()
                        .errors(ex.getMessage())
                        .build());
    }

    /**
     * Handle bad credentials exceptions
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<WebResponse<String>> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {


        Span span = tracer.activeSpan();

        if (span != null) {
            span.setTag("error", true);
            span.setTag("error.msg", "Invalid email or password");
            span.setTag("error.type", "BadCredentials");
            span.setTag("error.category", "authentication");
            span.setTag("error.severity", "warning");
            span.setTag("request.path", request.getDescription(false));

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Invalid email or password");
            errorDetails.put("path", request.getDescription(false));
            span.log(errorDetails);
        }

        logger.warn("Bad credentials: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(WebResponse.<String>builder()
                        .errors("Invalid email or password")
                        .build());
    }

    /**
     * Handle bad request exceptions
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<WebResponse<String>> handleBadRequestException(
            BadRequestException ex, WebRequest request) {


        Span span = tracer.activeSpan();

        if (span != null) {
            span.setTag("error", true);
            span.setTag("error.msg", ex.getMessage());
            span.setTag("error.type", "BadRequest");
            span.setTag("error.category", "validation");
            span.setTag("error.severity", "warning");
            span.setTag("request.path", request.getDescription(false));

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", ex.getMessage());
            errorDetails.put("path", request.getDescription(false));
            span.log(errorDetails);
        }

        logger.warn("Bad request: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.<String>builder()
                        .errors(ex.getMessage())
                        .build());
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<WebResponse<String>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {


        Span span = tracer.activeSpan();

        if (span != null) {
            span.setTag("error", true);
            span.setTag("error.msg", ex.getMessage());
            span.setTag("error.type", "IllegalArgument");
            span.setTag("error.category", "validation");
            span.setTag("error.severity", "warning");
            span.setTag("request.path", request.getDescription(false));

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", ex.getMessage());
            errorDetails.put("path", request.getDescription(false));
            span.log(errorDetails);
        }

        logger.warn("Illegal argument: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.<String>builder()
                        .errors(ex.getMessage())
                        .build());
    }

    /**
     * Helper method to convert stack trace to string
     */
    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}