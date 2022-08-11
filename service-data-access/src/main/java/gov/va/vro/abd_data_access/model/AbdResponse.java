package gov.va.vro.abd_data_access.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbdResponse {
  private String veteranIcn;
  private String diagnosticCode;
  @JsonInclude(JsonInclude.Include.ALWAYS)
  private AbdEvidence evidence;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
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
