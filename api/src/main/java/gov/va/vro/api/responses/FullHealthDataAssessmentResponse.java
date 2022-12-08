package gov.va.vro.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.va.vro.model.AbdEvidence;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class FullHealthDataAssessmentResponse {
  @NonNull
  @Schema(description = "Veteran medical internal control number (EHR id)", example = "90653535")
  private String veteranIcn;

  @NonNull
  @Schema(description = "Claim submission id", example = "1234")
  private String claimSubmissionId;

  @Schema(description = "Diagnostic code for the claim contention", example = "7101")
  private String diagnosticCode;

  @Schema(description = "Medical evidence supporting assessment")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private AbdEvidence evidence;

  @Schema(description = "Error message in the case of an error")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, Object> errors;

  @Schema(description = "Evidence summary fields")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, Object> evidenceSummary;

  @Schema(description = "Calculated evidence fields")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, Object> calculated;

  @Schema(description = "Decision based on available data")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Boolean sufficientForFastTracking;

  @Schema(description = "Date of claim or date of processing if not provided")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String dateOfClaim;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String disabilityActionType;

  @Schema(description = "Error message in the case of an error")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String errorMessage;

  @Schema(description = "Status")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String status;

  /***
   *<p>Summary.</p>
   *
   * @param veteranIcn veteran ICN number
   *
   * @param diagnosticCode diagnostic code
   *
   * @param errorMessage error message
   */
  public FullHealthDataAssessmentResponse(
      String veteranIcn, String diagnosticCode, String errorMessage) {
    this.veteranIcn = veteranIcn;
    this.diagnosticCode = diagnosticCode;
    this.errorMessage = errorMessage;
  }
}
