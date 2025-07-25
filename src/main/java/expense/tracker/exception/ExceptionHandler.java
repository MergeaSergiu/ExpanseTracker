package expense.tracker.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;
import java.time.LocalDateTime;


@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleEntityExistsException(EntityExistsException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError fieldError = (FieldError) ex.getBindingResult().getAllErrors().getFirst();
        String errorMessage = fieldError.getDefaultMessage();

        ErrorResponse errorResponse = new ErrorResponse(
                errorMessage,
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(IOException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }




}
