package gov.va.vro.controller.exception;

import gov.va.vro.api.model.ClaimProcessingException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClaimProcessingError {

  private String claimSubmissionId;
  private String message;

  public ClaimProcessingError(ClaimProcessingException exception) {
    this.claimSubmissionId = exception.getClaimSubmissionId();
    this.message = exception.getMessage();
  }

  public ClaimProcessingError(String message) {
    this.claimSubmissionId = null;
    this.message = message;
  }
}
