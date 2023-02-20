package gov.va.vro.mockmas.controller;

import gov.va.vro.mockmas.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles response status exceptions from controllers.
   *
   * @param exception the exception
   * @return http response
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleException(ResponseStatusException exception) {
    log.info("Expected thrown exception", exception);
    ErrorResponse error = new ErrorResponse();
    error.setMessage(exception.getReason());
    return new ResponseEntity<>(error, exception.getStatus());
  }

  /**
   * Handles general, unspecified exceptions (catch-all).
   *
   * @param exception the exception
   * @return returns exception
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception exception) {
    log.error("Unexpected error", exception);
    ErrorResponse error = new ErrorResponse();
    error.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
