package gov.va.vro.controller.exception;

import gov.va.vro.api.model.ClaimProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ClaimProcessingError> generalException(Exception exception) {
    return new ResponseEntity<>(
        new ClaimProcessingError(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(ClaimProcessingException.class)
  public ResponseEntity<ClaimProcessingError> claimProcessingException(
      ClaimProcessingException exception) {
    return new ResponseEntity<>(new ClaimProcessingError(exception), exception.getHttpStatus());
  }
}
