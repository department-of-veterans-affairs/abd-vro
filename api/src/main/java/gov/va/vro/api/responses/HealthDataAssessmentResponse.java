package gov.va.vro.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.va.vro.api.model.AbdEvidence;
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

  @NonNull
  @Schema(description = "Diagnostic code for the claim contention", example = "7101")
  private String diagnosticCode;

  @Schema(description = "Medical evidence supporting assessment")
  @JsonInclude
  private AbdEvidence evidence;

  @Schema(description = "Error message in the case of an error")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String errorMessage;

  public HealthDataAssessmentResponse(
      String veteranIcn, String diagnosticCode, String errorMessage) {
    this.veteranIcn = veteranIcn;
    this.diagnosticCode = diagnosticCode;
    this.errorMessage = errorMessage;
  }
}
