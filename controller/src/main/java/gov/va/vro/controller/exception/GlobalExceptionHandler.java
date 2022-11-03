package gov.va.vro.controller.exception;

import gov.va.vro.api.model.ClaimProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ClaimProcessingError> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException exception) {
    log.error("Validation error", exception);
    final StringBuffer errors = new StringBuffer();
    for (final FieldError error : exception.getBindingResult().getFieldErrors()) {
      if (!errors.isEmpty()) {
        errors.append("\n");
      }
      errors.append(error.getField() + ": " + error.getDefaultMessage());
    }
    ClaimProcessingError cpe = new ClaimProcessingError(errors.toString());
    return new ResponseEntity<>(cpe, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ClaimProcessingError> handleException(Exception exception) {
    log.error("Unexpected error", exception);
    ClaimProcessingError cpe =
        new ClaimProcessingError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    return new ResponseEntity<>(cpe, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ClaimProcessingException.class)
  public ResponseEntity<ClaimProcessingError> handleClaimProcessingException(
      ClaimProcessingException exception) {
    return new ResponseEntity<>(new ClaimProcessingError(exception), exception.getHttpStatus());
  }
}
