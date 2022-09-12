package gov.va.vro.api.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ClaimProcessingException extends Exception {

  @Getter private final String claimSubmissionId;
  @Getter private final HttpStatus httpStatus;

  /***
   *<p>Summary.</p>
   *
   * @param claimSubmissionId claim submission ID
   * @param httpStatus HTTP Status
   * @param message message
   */
  public ClaimProcessingException(String claimSubmissionId, HttpStatus httpStatus, String message) {
    super(message);
    this.claimSubmissionId = claimSubmissionId;
    this.httpStatus = httpStatus;
  }

  /***
   *<p>Summary.</p>
   *
   * @param claimSubmissionId claim submission id
   *
   * @param httpStatus http status
   *
   * @param exception exception
   */
  public ClaimProcessingException(
      String claimSubmissionId, HttpStatus httpStatus, Exception exception) {
    super(exception);
    this.claimSubmissionId = claimSubmissionId;
    this.httpStatus = httpStatus;
  }

  /***
   *<p>Summary.</p>
   *
   * @return returns the message
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
