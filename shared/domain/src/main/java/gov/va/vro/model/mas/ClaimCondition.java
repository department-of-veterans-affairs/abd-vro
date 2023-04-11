package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimCondition {

  private String name;

  @NotNull
  @Schema(description = "Diagnostic code", example = "7101")
  @NotBlank(message = "Diagnostic Code is required")
  private String diagnosticCode;

  @Schema(description = "Disability action type", example = "NEW")
  private String disabilityActionType;

  @Schema(description = "Disability classification code", example = "3460")
  private String disabilityClassificationCode;

  private String ratedDisabilityId;
}
