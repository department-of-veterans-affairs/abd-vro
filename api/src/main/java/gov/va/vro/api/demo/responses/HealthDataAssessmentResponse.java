package gov.va.vro.api.demo.responses;

import gov.va.vro.api.demo.model.AbdEvidence;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class HealthDataAssessmentResponse {
  @NonNull
  @Schema(description = "Veteran medical internal control number (EHR id)", example = "90653535")
  private String veteranIcn;

  @Schema(description = "Diagnostic code for the claim contention", example = "7101")
  private int diagnosticCode;

  @Schema(description = "Medical evidence supporting assessment")
  private AbdEvidence evidence;

  @Schema(description = "Error message in the case of an error")
  private String errorMessage;

  public HealthDataAssessmentResponse(String veteranIcn, int diagnosticCode, String errorMessage) {
    this.veteranIcn = veteranIcn;
    this.diagnosticCode = diagnosticCode;
    this.errorMessage = errorMessage;
  }
}
