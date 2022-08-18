package gov.va.vro.controller.exception;

import gov.va.vro.api.model.ClaimProcessingException;
import lombok.Getter;

@Getter
public class ClaimProcessingError {

  private final String claimSubmissionId;
  private final String message;

  public ClaimProcessingError(ClaimProcessingException exception) {
    this.claimSubmissionId = exception.getClaimSubmissionId();
    this.message = exception.getMessage();
  }

  public ClaimProcessingError(String message) {
    this.claimSubmissionId = null;
    this.message = message;
  }
}
