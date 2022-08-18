package gov.va.vro.api.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(
    name = "HealthDataAssessmentRequest",
    description = "Claim details for the health data assessment")
public class HealthDataAssessmentRequest {
  @NotBlank(message = "Veteran Icn cannot be empty")
  @Schema(description = "Veteran medical internal control number (EHR id)", example = "9000682")
  private String veteranIcn;

  @NotBlank(message = "Diagnostic code cannot be empty")
  @Schema(description = "Diagnostic code for the claim contention", example = "7101")
  private String diagnosticCode;

  @NotBlank(message = "Claim submission id cannot be empty")
  @Schema(description = "Claim submission id", example = "1234")
  private String claimSubmissionId;
}
