package gov.va.vro.mocklh.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles response status exceptions from controllers.
   *
   * @param exception the exception
   * @return http response
   */
  @ExceptionHandler(HttpStatusCodeException.class)
  public ResponseEntity<String> handleException(HttpStatusCodeException exception) {
    log.info("Expected thrown exception", exception);
    String responseBody = exception.getResponseBodyAsString();
    return new ResponseEntity<>(responseBody, exception.getStatusCode());
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
    String body = String.format("{\"message\":\"%s\"}", message);
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
