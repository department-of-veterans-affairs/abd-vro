package gov.va.vro.mockbipclaims.controller;

import gov.va.vro.mockbipclaims.model.bip.Message;
import gov.va.vro.mockbipclaims.model.bip.ProviderResponse;
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
  public ResponseEntity<ProviderResponse> handleAuthenticationException(
      AuthenticationException ex) {
    log.error("Authentication error", ex);
    Message message = new Message();
    message.setText(HttpStatus.UNAUTHORIZED.getReasonPhrase());
    ProviderResponse response = new ProviderResponse();
    response.addMessagesItem(message);
    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
  }

  /**
   * Handles response status exceptions from controllers.
   *
   * @param exception the exception
   * @return http response
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ProviderResponse> handleException(ResponseStatusException exception) {
    log.info("Expected thrown exception", exception);
    Message message = new Message();
    message.setText(exception.getReason());
    ProviderResponse response = new ProviderResponse();
    response.addMessagesItem(message);
    return new ResponseEntity<>(response, exception.getStatus());
  }

  /**
   * Handles general, unspecified exceptions (catch-all).
   *
   * @param exception the exception
   * @return returns exception
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProviderResponse> handleException(Exception exception) {
    log.error("Unexpected error", exception);
    Message message = new Message();
    message.setText(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    ProviderResponse response = new ProviderResponse();
    response.addMessagesItem(message);
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
