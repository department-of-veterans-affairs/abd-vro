package gov.va.vro.abddataaccess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class HealthDataAssessment {
  @NotBlank
  @Schema(description = "Veteran medical internal control number (EHR id)", example = "90653535")
  private String veteranIcn;

  @Schema(description = "Claim submission id", example = "1234")
  private String claimSubmissionId;

  @NotBlank
  @Schema(description = "Diagnostic code for the claim contention", example = "7101")
  private String diagnosticCode;

  @Schema(description = "Medical evidence supporting assessment")
  @JsonInclude
  private AbdEvidence evidence;

  @Schema(description = "Error message in the case of an error")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String errorMessage;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String disabilityActionType;

  @Schema(description = "date of the Claim")
  private String claimSubmissionDateTime;

  @JsonIgnore private HealthAssessmentSource source = HealthAssessmentSource.LIGHTHOUSE;
}
