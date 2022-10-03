package gov.va.vro.api.model.mas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
  private String diagnosticcode;

  private String disabilityactiontype;
  private String disabilityclassificationcode;
  private String rateddisabilityid;
}
