package gov.va.vro.abddataaccess.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.va.vro.model.AbdEvidence;
import lombok.Getter;
import lombok.Setter;

/** It represents a response object for a claim evidence request. */
@Getter
@Setter
public class AbdResponse {
  private String veteranIcn;
  private String diagnosticCode;

  private String claimSubmissionId;

  @JsonInclude(JsonInclude.Include.ALWAYS)
  private AbdEvidence evidence;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String errorMessage;

  public AbdResponse() {}

  /**
   * Constructs the object with the values provided.
   *
   * @param claim a claim.
   */
  public AbdResponse(AbdClaim claim) {
    veteranIcn = claim.getVeteranIcn();
    diagnosticCode = claim.getDiagnosticCode();
    claimSubmissionId = claim.getClaimSubmissionId();
  }

  /**
   * Constructs the object with the initial values.
   *
   * @param claim a claim.
   * @param evidence the claim evidence.
   */
  public AbdResponse(AbdClaim claim, AbdEvidence evidence) {
    veteranIcn = claim.getVeteranIcn();
    diagnosticCode = claim.getDiagnosticCode();
    claimSubmissionId = claim.getClaimSubmissionId();
    this.evidence = evidence;
  }

  /**
   * Constructs the object for a claim and an error message.
   *
   * @param claim a claim.
   * @param errorMessage error message.
   */
  public AbdResponse(AbdClaim claim, String errorMessage) {
    veteranIcn = claim.getVeteranIcn();
    diagnosticCode = claim.getDiagnosticCode();
    claimSubmissionId = claim.getClaimSubmissionId();
    this.errorMessage = errorMessage;
  }
}
