package gov.va.vro.api.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ClaimProcessingException extends Exception {

  @Getter private final String claimSubmissionId;
  @Getter private final HttpStatus httpStatus;
  private Exception exception;
  private String message;

  public ClaimProcessingException(String claimSubmissionId, HttpStatus httpStatus, String message) {
    super(message);
    this.claimSubmissionId = claimSubmissionId;
    this.httpStatus = httpStatus;
    this.message = message;
  }

  public ClaimProcessingException(
      String claimSubmissionId, HttpStatus httpStatus, Exception exception) {
    super(exception);
    this.claimSubmissionId = claimSubmissionId;
    this.httpStatus = httpStatus;
    this.exception = exception;
  }

  public String getOriginalMessage() {
    if (exception == null) {
      return message;
    }
    if (exception.getCause() == null) {
      return exception.getMessage();
    }
    return exception.getCause().getMessage();
  }
}
