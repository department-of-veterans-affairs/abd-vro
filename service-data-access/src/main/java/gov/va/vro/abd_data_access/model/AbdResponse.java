package gov.va.vro.abd_data_access.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbdResponse {
  private String veteranIcn;
  private int diagnosticCode;
  private AbdEvidence evidence;
  private String errorMessage;

  public AbdResponse() {}

  public AbdResponse(AbdClaim claim) {
    veteranIcn = claim.getVeteranIcn();
    diagnosticCode = claim.getDiagnosticCode();
  }

  public AbdResponse(AbdClaim claim, AbdEvidence evidence) {
    veteranIcn = claim.getVeteranIcn();
    diagnosticCode = claim.getDiagnosticCode();
    this.evidence = evidence;
  }

  public AbdResponse(AbdClaim claim, String errorMessage) {
    veteranIcn = claim.getVeteranIcn();
    diagnosticCode = claim.getDiagnosticCode();
    this.errorMessage = errorMessage;
  }
}
