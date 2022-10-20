package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimCondition {

  private String name;

  @Schema(description = "Diagnostic code", example = "1111")
  @NotBlank(message = "Diagnostic Code is required")
  private String diagnosticCode;

  @Schema(description = "Disability action type", example = "INCREASE")
  private String disabilityActionType;

  @Schema(description = "Disability classification code", example = "3460")
  private String disabilityClassificationCode;

  private String ratedDisabilityId;
}
