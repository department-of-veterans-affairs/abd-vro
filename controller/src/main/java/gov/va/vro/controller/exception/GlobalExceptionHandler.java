package gov.va.vro.controller.exception;

import com.fasterxml.jackson.core.JsonParseException;
import gov.va.vro.api.model.ClaimProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles method argument not valid.
   *
   * @param exception the exception
   * @return returns exception
   */
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

  /**
   * Handles method argument not valid type.
   *
   * @param exception the exception
   * @return returns exception
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ClaimProcessingError> handleMethodArgumentNotValidException(
      MethodArgumentTypeMismatchException exception) {
    log.error("Validation error", exception);
    MethodParameter parameter = exception.getParameter();
    String name = parameter.getParameterName() + " is of wrong type.";
    ClaimProcessingError cpe = new ClaimProcessingError(name);
    return new ResponseEntity<>(cpe, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles constraint violations such as min or max limits.
   *
   * @param exception the exception
   * @return returns exception
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ClaimProcessingError> handleMethodArgumentNotValidException(
      ConstraintViolationException exception) {
    log.error("Validation error", exception);
    ClaimProcessingError cpe = new ClaimProcessingError("invalid parameters");
    return new ResponseEntity<>(cpe, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles exception.
   *
   * @param exception the exception
   * @return returns new exception
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ClaimProcessingError> handleException(Exception exception) {
    log.error("Unexpected error", exception);
    ClaimProcessingError cpe =
        new ClaimProcessingError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    return new ResponseEntity<>(cpe, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handles claim processing exception.
   *
   * @param exception exception.
   * @return returns claim processing error
   */
  @ExceptionHandler(ClaimProcessingException.class)
  public ResponseEntity<ClaimProcessingError> handleClaimProcessingException(
      ClaimProcessingException exception) {
    return new ResponseEntity<>(new ClaimProcessingError(exception), exception.getHttpStatus());
  }

  /**
   * handles json parsing.
   *
   * @param exception the exception
   * @return returns claim processing error
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ClaimProcessingError> handleJsonParseException(
      JsonParseException exception) {
    log.error("Bad Request: Malformed JSON", exception);
    ClaimProcessingError cpe = new ClaimProcessingError(HttpStatus.BAD_REQUEST.getReasonPhrase());
    return new ResponseEntity<>(cpe, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles unsupported Http Methods.
   *
   * @param exception exception
   * @return new exception
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ClaimProcessingError> handleUnsupportedHttpMethodException(
      HttpRequestMethodNotSupportedException exception) {
    log.error("HTTP Method Not Supported");
    ClaimProcessingError cpe =
        new ClaimProcessingError(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
    return new ResponseEntity<ClaimProcessingError>(cpe, HttpStatus.METHOD_NOT_ALLOWED);
  }
}
