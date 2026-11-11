package pl.zdaze.common;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Turns every error into {@code application/problem+json} with an extra {@code code} field and an
 * English {@code detail} that the frontend can show as is (docs/api.md, "Błędy").
 */
@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /** One entry of the {@code errors} list in a validation error. */
    public record ValidationError(String field, String message) {}

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();
        ProblemDetail body = ex.getBody();
        body.setProperty("errors", errors);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    /** Every Spring MVC exception (unknown path, bad JSON, invalid input...) ends up here. */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ResponseEntity<Object> response = super.handleExceptionInternal(ex, body, headers, statusCode, request);
        if (response != null && response.getBody() instanceof ProblemDetail problem) {
            problem.setProperty("code", codeFor(statusCode).name());
            problem.setDetail(detailFor(statusCode));
        }
        return response;
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> handleUnexpected(Exception ex) {
        log.error("Unexpected error while handling a request", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detailFor(status));
        problem.setProperty("code", ErrorCode.INTERNAL_ERROR.name());
        return ResponseEntity.status(status).body(problem);
    }

    private static ErrorCode codeFor(HttpStatusCode status) {
        if (status.value() == 404) {
            return ErrorCode.NOT_FOUND;
        }
        if (status.is4xxClientError()) {
            return ErrorCode.VALIDATION_ERROR;
        }
        return ErrorCode.INTERNAL_ERROR;
    }

    private static String detailFor(HttpStatusCode status) {
        if (status.value() == 404) {
            return "Resource not found.";
        }
        if (status.value() == 400) {
            return "Invalid input.";
        }
        if (status.is4xxClientError()) {
            return "Invalid request.";
        }
        return "An unexpected server error occurred. Please try again later.";
    }
}
