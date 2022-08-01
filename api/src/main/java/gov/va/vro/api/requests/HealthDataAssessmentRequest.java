package gov.va.vro.api.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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

  @NonNull
  @Schema(description = "Diagnostic code for the claim contention", example = "7101")
  private String diagnosticCode;

  @Schema(description = "Claim number", example = "1234")
  private String claimSubmissionId;
}
