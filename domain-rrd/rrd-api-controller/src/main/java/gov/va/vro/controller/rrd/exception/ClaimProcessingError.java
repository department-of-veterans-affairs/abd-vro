package gov.va.vro.controller.rrd.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.va.vro.api.rrd.model.ClaimProcessingException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClaimProcessingError {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String claimSubmissionId;

  private String message;

  public ClaimProcessingError(ClaimProcessingException exception) {
    this.claimSubmissionId = exception.getClaimSubmissionId();
    this.message = exception.getOriginalMessage();
  }

  public ClaimProcessingError(String message) {
    this.claimSubmissionId = null;
    this.message = message;
  }
}
