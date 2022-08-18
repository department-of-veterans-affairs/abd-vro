package gov.va.vro.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.va.vro.api.model.AbdEvidence;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class HealthData7101AssessmentResponse {
  @NotBlank
  @Schema(description = "Veteran medical internal control number (EHR id)", example = "90653535")
  private String veteranIcn;

  @NotBlank
  @Schema(description = "Diagnostic code for the claim contention", example = "7101")
  private String diagnosticCode;

  @Schema(description = "Medical evidence supporting assessment")
  @JsonInclude
  private AbdEvidence evidence;

  @Schema(description = "Error message in the case of an error")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, Object> errors;

  @Schema(description = "Calculated fields")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, Object> calculated;

  @Schema(description = "Error message in the case of an error")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String errorMessage;

  @Schema(description = "Status")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private String status;

  public HealthData7101AssessmentResponse(
      String veteranIcn, String diagnosticCode, String errorMessage) {
    this.veteranIcn = veteranIcn;
    this.diagnosticCode = diagnosticCode;
    this.errorMessage = errorMessage;
  }
}
