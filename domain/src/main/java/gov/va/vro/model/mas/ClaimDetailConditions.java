package gov.va.vro.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaimDetailConditions {

  private String name;

  @NotBlank(message = "Diagnostic Code is required")
  private String diagnosticCode;

  private String disabilityActionType;

  private String disabilityClassificationCode;

  private String ratedDisabilityId;
}
