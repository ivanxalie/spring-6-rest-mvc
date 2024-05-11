package guru.springframework.spring6restmvc.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class CustomErrorController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<?> errors = e
                .getFieldErrors()
                .stream()
                .map(fieldError -> Collections
                        .singletonMap(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<?> handleJPAViolations(TransactionSystemException e) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.badRequest();
        if (e.getCause().getCause() instanceof ConstraintViolationException violationException) {
            List<?> errors = violationException.getConstraintViolations()
                    .stream()
                    .map(constraintViolation -> Collections
                            .singletonMap(
                                    constraintViolation.getPropertyPath(),
                                    constraintViolation.getMessage()))
                    .toList();
            return builder.body(errors);
        }
        return builder.build();
    }
}
