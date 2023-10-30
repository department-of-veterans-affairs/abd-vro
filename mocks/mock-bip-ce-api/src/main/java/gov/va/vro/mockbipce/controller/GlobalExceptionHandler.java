package gov.va.vro.mockbipce.controller;

import gov.va.vro.bip.model.evidence.response.VefsErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles authentication errors.
   *
   * @param ex the exception
   * @return returns exception
   */
  @ExceptionHandler({AuthenticationException.class})
  public ResponseEntity<VefsErrorResponse> handleAuthenticationException(
      AuthenticationException ex) {
    log.error("Authentication error", ex);
    VefsErrorResponse cpe =
        VefsErrorResponse.builder().message(HttpStatus.UNAUTHORIZED.getReasonPhrase()).build();
    return new ResponseEntity<>(cpe, HttpStatus.UNAUTHORIZED);
  }

  /**
   * Handles response status exceptions from controllers.
   *
   * @param exception the exception
   * @return http response
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<VefsErrorResponse> handleException(ResponseStatusException exception) {
    log.info("Expected thrown exception", exception);
    VefsErrorResponse cpe = VefsErrorResponse.builder().message(exception.getReason()).build();
    return new ResponseEntity<>(cpe, exception.getStatusCode());
  }

  /**
   * Handles general, unspecified exceptions (catch-all).
   *
   * @param exception the exception
   * @return returns exception
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<VefsErrorResponse> handleException(Exception exception) {
    log.error("Unexpected error", exception);
    VefsErrorResponse cpe =
        VefsErrorResponse.builder()
            .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .build();
    return new ResponseEntity<>(cpe, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
