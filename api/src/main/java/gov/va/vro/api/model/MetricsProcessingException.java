package gov.va.vro.api.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class MetricsProcessingException extends Exception {

  @Getter private final HttpStatus httpStatus;

  /***
   * <p> Metrics Processing Exception with string message.</p>
   *
   * @param httpStatus HTTP Status code
   *
   * @param message Error message
   */
  public MetricsProcessingException(HttpStatus httpStatus, String message) {
    super(message);
    this.httpStatus = httpStatus;
  }

  /***
   * <p>Metrics Processing Exception with exception message.</p>
   *
   * @param httpStatus HTTP Status
   *
   * @param exception Exception
   */
  public MetricsProcessingException(HttpStatus httpStatus, Exception exception) {
    super(exception);
    this.httpStatus = httpStatus;
  }

  /***
   * <p>Returns the original message.</p>
   *
   * @return Message returned
   */
  public String getOriginalMessage() {
    Throwable exception = super.getCause();
    if (exception == null) {
      return this.getMessage();
    }
    if (exception.getCause() == null) {
      return exception.getMessage();
    }
    return exception.getCause().getMessage();
  }
}
