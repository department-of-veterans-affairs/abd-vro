package gov.va.vro.api.demo.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(
    name = "HealthDataAssessmentRequest",
    description = "Claim details for the health data assessment")
public class HealthDataAssessmentRequest {
  @NonNull
  @Schema(description = "Veteran medical internal control number (EHR id)", example = "9000682")
  private String veteranIcn;

  @Schema(description = "Diagnostic code for the claim contention", example = "7101")
  private int diagnosticCode;

  private String claimSubmissionId;
}
