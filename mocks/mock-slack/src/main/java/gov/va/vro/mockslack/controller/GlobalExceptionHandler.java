package gov.va.vro.mockslack.controller;

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
  public ResponseEntity<String> handleException(ResponseStatusException exception) {
    log.info("Expected thrown exception", exception);
    return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
  }

  /**
   * Handles invalid (not a number) collection id exception.
   *
   * @param exception the exception
   * @return http response
   */
  @ExceptionHandler(NumberFormatException.class)
  public ResponseEntity<String> handleException(NumberFormatException exception) {
    String message = "Invalid collection id";
    log.info(message, exception);
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles general, unspecified exceptions (catch-all).
   *
   * @param exception the exception
   * @return returns exception
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception exception) {
    log.error("Unexpected error", exception);
    String message = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();
    return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
