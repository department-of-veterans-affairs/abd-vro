package gov.va.vro.api.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ClaimProcessingException extends Exception {

  @Getter private final String claimSubmissionId;
  @Getter private final HttpStatus httpStatus;

  public ClaimProcessingException(String claimSubmissionId, HttpStatus httpStatus, String message) {
    super(message);
    this.claimSubmissionId = claimSubmissionId;
    this.httpStatus = httpStatus;
  }

  public ClaimProcessingException(
      String claimSubmissionId, HttpStatus httpStatus, Exception exception) {
    super(exception);
    this.claimSubmissionId = claimSubmissionId;
    this.httpStatus = httpStatus;
  }

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
